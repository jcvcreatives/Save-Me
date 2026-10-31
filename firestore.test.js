const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validPayload(uid = ALICE_UID, overrides = {}) {
  const pastDate = new Date(Date.now() - 2000);
  return {
    userId: uid,
    displayName: "Alice Doodle",
    email: "alice@example.com",
    authMethod: "gmail",
    campaignLevelIndex: 4,
    coins: 1250,
    perfectCount: 3,
    currentStreak: 2,
    maxStreak: 5,
    completedLevelsCount: 4,
    createdAt: pastDate,
    updatedAt: pastDate,
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS (Dirty Dozen) ---

test("1. Unauthenticated user: cannot read player account", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("players").doc(ALICE_UID).get());
});

test("2. Authenticated user: cannot read another user's player account (PII isolation)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("players").doc(ALICE_UID).set(validPayload(ALICE_UID)));

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("players").doc(ALICE_UID).get());
});

test("3. Authenticated user: can create and read their own player account", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("players").doc(ALICE_UID).set(validPayload(ALICE_UID)));
  await assertSucceeds(aliceDb.collection("players").doc(ALICE_UID).get());
});

test("4. Identity spoofing on create: cannot set userId to another UID", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("players").doc(ALICE_UID).set(validPayload(BOB_UID))
  );
});

test("5. Cross-path create: cannot create document under another user's ID", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("players").doc(BOB_UID).set(validPayload(ALICE_UID))
  );
});

test("6. Shadow field injection on create: rejects unexpected fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("players").doc(ALICE_UID).set(
      validPayload(ALICE_UID, { isAdmin: true })
    )
  );
});

test("7. Shadow field injection on update: rejects unexpected fields", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("players").doc(ALICE_UID).set(validPayload(ALICE_UID)));
  await assertFails(
    aliceDb.collection("players").doc(ALICE_UID).update({
      isVerified: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("8. Valid update: owner can update displayName, coins, and campaignLevelIndex", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("players").doc(ALICE_UID).set(validPayload(ALICE_UID)));
  await assertSucceeds(
    aliceDb.collection("players").doc(ALICE_UID).update({
      displayName: "Sketch Master Alice",
      coins: 1500,
      campaignLevelIndex: 10,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("9. Immutable userId on update: cannot change userId after creation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("players").doc(ALICE_UID).set(validPayload(ALICE_UID)));
  await assertFails(
    aliceDb.collection("players").doc(ALICE_UID).update({
      userId: BOB_UID,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("10. Value poisoning: rejects empty displayName or out-of-range coins", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("players").doc(ALICE_UID).set(
      validPayload(ALICE_UID, { displayName: "" })
    )
  );
  await assertFails(
    aliceDb.collection("players").doc(ALICE_UID).set(
      validPayload(ALICE_UID, { coins: -10 })
    )
  );
});

test("11. Enum poisoning: rejects invalid authMethod", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("players").doc(ALICE_UID).set(
      validPayload(ALICE_UID, { authMethod: "anonymous" })
    )
  );
});

test("12. Client query alignment: fails unfiltered list query on players", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("players").get());
});

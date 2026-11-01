const { test, before, after, beforeEach } = require('node:test');
const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds
} = require('@firebase/rules-unit-testing');
const { readFileSync } = require('node:fs');

let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: 'demo-no-project',
    firestore: {
      rules: readFileSync('firestore.rules', 'utf8'),
    },
  });
});

beforeEach(async () => {
  await testEnv.clearFirestore();
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

test('authenticated user can create and merge their own /players/{userId} document', async () => {
  const aliceDb = testEnv.authenticatedContext('alice_123').firestore();
  const aliceRef = aliceDb.collection('players').doc('alice_123');
  const now = new Date();

  await assertSucceeds(
    aliceRef.set({
      userId: 'alice_123',
      displayName: 'Alice Doodle',
      email: 'alice@example.com',
      authMethod: 'account',
      campaignLevelIndex: 4,
      coins: 1500,
      perfectCount: 3,
      currentStreak: 2,
      maxStreak: 5,
      completedLevelsCount: 4,
      createdAt: now,
      updatedAt: now
    }, { merge: true })
  );

  await assertSucceeds(aliceRef.get());

  // Subsequent merge update on sign-in or state change
  await assertSucceeds(
    aliceRef.set({
      userId: 'alice_123',
      displayName: 'Alice Updated',
      email: 'alice@example.com',
      authMethod: 'gmail',
      campaignLevelIndex: 10,
      coins: 2100,
      perfectCount: 5,
      currentStreak: 4,
      maxStreak: 6,
      completedLevelsCount: 10,
      updatedAt: new Date()
    }, { merge: true })
  );
});

test('unauthenticated user cannot read or write /players/{userId}', async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const ref = unauthDb.collection('players').doc('alice_123');

  await assertFails(ref.get());
  await assertFails(
    ref.set({
      userId: 'alice_123',
      displayName: 'Anon',
      authMethod: 'account',
      campaignLevelIndex: 0,
      coins: 100,
      updatedAt: new Date()
    })
  );
});

test('cross-user read, update, and delete are denied', async () => {
  const aliceDb = testEnv.authenticatedContext('alice_123').firestore();
  const bobDb = testEnv.authenticatedContext('bob_456').firestore();

  await assertSucceeds(
    aliceDb.collection('players').doc('alice_123').set({
      userId: 'alice_123',
      displayName: 'Alice',
      email: 'alice@example.com',
      authMethod: 'email',
      campaignLevelIndex: 1,
      coins: 500,
      updatedAt: new Date()
    })
  );

  const bobRefToAlice = bobDb.collection('players').doc('alice_123');
  await assertFails(bobRefToAlice.get());
  await assertFails(
    bobRefToAlice.update({
      displayName: 'Hacked',
      authMethod: 'account',
      campaignLevelIndex: 99,
      coins: 999999,
      updatedAt: new Date()
    })
  );
  await assertFails(bobRefToAlice.delete());
});

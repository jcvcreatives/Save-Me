# Security Specification (`security_spec.md`)

## 1. Data Invariants
1. **Strict Ownership Isolation**: A `/players/{userId}` document can only be read (`get`, `list`), created, updated, or deleted by an authenticated user whose `request.auth.uid == userId` and `data.userId == request.auth.uid`.
2. **PII Isolation**: Because `email` is optional PII on `/players/{userId}`, no non-owner or unauthenticated user can ever read or query another user's document.
3. **Immutable Identity & Creation Timestamp**: On `update`, `userId` and `createdAt` must strictly equal `resource.data.userId` and `resource.data.createdAt`.
4. **Server Timestamp Bounds**: `createdAt` and `updatedAt` must be Firestore `timestamp` objects with `<= request.time`.
5. **Bounded Strings & Numbers**: `displayName` must be 1..50 chars, optional `email` must be <= 150 chars, `authMethod` must be `'gmail'` or `'account'`, and numeric stats (`campaignLevelIndex`, `coins`, `perfectCount`, `currentStreak`, `maxStreak`, `completedLevelsCount`) must be non-negative integers within valid game bounds.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Read**: `get /players/alice_123` with `auth == null` -> REJECT.
2. **Cross-User Read (PII Leak Attempt)**: `get /players/bob_456` as `alice_123` -> REJECT.
3. **Unfiltered Collection List**: `list /players` as `alice_123` without `where("userId", "==", "alice_123")` -> REJECT.
4. **Identity Spoofing on Create**: `create /players/alice_123` with `userId: "bob_456"` -> REJECT.
5. **Cross-Path Create**: `create /players/bob_456` as `alice_123` -> REJECT.
6. **Shadow Field Injection on Create**: `create /players/alice_123` with extra field `isAdmin: true` -> REJECT.
7. **Shadow Field Injection on Update**: `update /players/alice_123` adding `isVerified: true` -> REJECT.
8. **Immutable `createdAt` Tampering on Update**: `update /players/alice_123` modifying `createdAt` -> REJECT.
9. **Immutable `userId` Tampering on Update**: `update /players/alice_123` modifying `userId` -> REJECT.
10. **Value Poisoning (`displayName` empty or > 50 chars)**: `create /players/alice_123` with `displayName: ""` -> REJECT.
11. **Enum Poisoning (`authMethod`)**: `create /players/alice_123` with `authMethod: "anonymous"` -> REJECT.
12. **Future Timestamp Injection**: `create /players/alice_123` with `createdAt` in the future (`> request.time`) -> REJECT.

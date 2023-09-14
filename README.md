## Project Stage
**Step1**:
 - create `user` and `role` model
 - configure **security** and **redis**
 - Why save token in _redis_:
   - Fast Access
   - Expiration Handling: Redis can automatically handle the expiration of tokens, simplifying session management.
     - Redis handles expiration through a mechanism called **key expiration**(`TTL`).

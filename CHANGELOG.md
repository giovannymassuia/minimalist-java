# 📜 Changelog

This document provides a high-level view of the changes introduced in different versions of
the `minimalist-java`
framework and its modules. The changelog follows the conventions
from [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [0.0.12-beta] - 2024-02-18

### 🛠️ `minimalist-api` Module

#### 🎉 Added

- `token bucket` rate limit implementation:
    - custom parameters for `bucketSize` and `refillRate`
    - global bucket
        - for now the implementation is global for all requests
    - how does it work:
        - T0 → bucket starts with 4 tokens
        - T1 → Request arrives, bucket has 4 tokens, request consumes 1 token.
        - T2 → Bucket has 3 tokens, more 3 requests arrive taking the remaining 3 tokens
        - T3 → Bucket is empty, new requests are dropped at this time
        - T4 → 4 tokens are refilled (this happens based on the refill rate set)
- `leaking bucket` rate limit implemenation
    - custom parameters for `bucketSize` and `leakRate`
    - global bucket
        - for now the implementation is global for all requests
        - how it works
            - T0 -> say bucket starts with 4 tokens, and leak rate is 2 seconds
            - T1 -> one request arrives, and is added to the bucket queue
            - T2 -> 2s has passed since last leak, request is pulled from queue respecting the 2
              seconds rate and processed
            - T3 -> 5 requests arrive at the same time, only 4 are added to the queue, and 1 is
              dropped
            - Tn -> every 2s the requests will be processed, so for 4 requests it could take up 8s
              to process all of them (4 req x 2 seconds for each leak)

## [0.0.10-beta] - 2023-11-01

### 🛠️ `minimalist-api` Module

#### 🎉Added

- `minimalist-api` module: first version of the `minimalist-api` module available

## [0.0.1-beta] - Release Date

### 🌐 `minimalist-java` Framework

- ci/cd pipeline

### 🛠️ `minimalist-api` Module

- ci/cd pipeline

### 💉 `minimalist-dependency-injection` Module

- no changes

## [0.0.0] - Init

- just a placeholder

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

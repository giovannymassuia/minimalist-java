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
- `leaking bucket` rate limit implementation:
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
- `fixed window counter` rate limit implementation:
    - custom parameters for `windowSize` and `requestLimit` per window.
    - global window:
        - The implementation is global for all requests, aligning to fixed time windows.
    - How does it work:
        - T0 → The time window starts, and the request counter is set to 0.
        - T1 → A request arrives within the window, the counter is incremented by 1.
        - T2 → More requests arrive and are counted until the limit is reached. For example, if
          the `requestLimit` is set to 5 and the `windowSize` is 10 seconds:
            - 2:00:00 → Window starts, counter is at 0.
            - 2:00:02 → 1st request arrives, counter is incremented to 1.
            - 2:00:04 → 2nd request arrives, counter is incremented to 2.
            - 2:00:06 → 3rd request arrives, counter is incremented to 3.
            - 2:00:08 → 4th and 5th requests arrive, counter is incremented to 5.
            - 2:00:09 → 6th request arrives, but since the counter has reached the `requestLimit`,
              this request is dropped.
        - T3 → The window resets at the end of the current window period (e.g., at 2:00:10), and the
          counter is set back to 0 for the next window.
        - T4 → New requests are again counted in the new window until the limit is reached. If more
          than the allowed number of requests are received within a window, extra requests are
          dropped.
    - Burst Traffic Handling:
        - The fixed window algorithm is susceptible to burst traffic at the edges of time windows.
          For example, if the `windowSize` is 1 minute and the system allows a maximum of 5 requests
          per minute:
            - Between 2:00:00 and 2:01:00, 5 requests are received and allowed.
            - Right at 2:01:00, a new window starts, and another 5 requests could be received and
              allowed by 2:02:00.
            - This could potentially allow bursts of up to 10 requests around the boundary of two
              windows (e.g., 5 requests at 2:00:59 and 5 more at 2:01:01), doubling the intended
              limit.

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

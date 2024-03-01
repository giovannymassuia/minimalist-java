import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

let metrics200 = new Counter('metrics_200');
let metrics429 = new Counter('metrics_429');

export const options = {
    vus: 5, // users at the same time
    duration: '10s'
    // rate: 5 // 5 iterations per second
};

export default function () {
    // const res = http.get('http://localhost:8080');

    const res = http.batch([
        ['GET', 'http://localhost:8080'],
        ['GET', 'http://localhost:8080'],
        ['GET', 'http://localhost:8080'],
        ['GET', 'http://localhost:8080'],
        ['GET', 'http://localhost:8080']
    ]);

    // check(res, {
    //     'is status 200': (r) => r.status === 200
    //     // 'is status 429': (r) => r.status === 429,
    // });

    // check all responses
    res.forEach((r) => {
        check(r, {
            'is status 200': (r) => r.status === 200,
            'is status 429': (r) => r.status === 429
        });
    });

    // switch (res.status) {
    //     case 200:
    //         metrics200.add(1);
    //         console.log(`[VU: ${__VU}][STATUS: ${res.status}] ${res.body}`);
    //         break;
    //     case 429:
    //         metrics429.add(1);
    //         console.error(`[VU: ${__VU}][STATUS: ${res.status}]`);
    //         break;
    // }

    // console.log(`[VU: ${__VU}][STATUS: ${res.status}] ${res.body}`); for all responses
    res.forEach((r) => {
        switch (r.status) {
            case 200:
                metrics200.add(1);
                console.log(`[VU: ${__VU}][STATUS: ${r.status}] ${r.body} ${r.timings.duration}`);
                break;
            case 429:
                metrics429.add(1);
                console.error(`[VU: ${__VU}][STATUS: ${r.status}] ${r.timings.duration}`);
                break;
        }
    });

    sleep(1);
}

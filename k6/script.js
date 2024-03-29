import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
  vus: 5, // users at the same time
  duration: '10s',
};

export default function () {
  const res = http.get('http://localhost:8080');
  check(res, {
    'is status 200': (r) => r.status === 200,
    // 'is status 429': (r) => r.status === 429,
  });
  sleep(1);
}

import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: __ENV.VUS ? parseInt(__ENV.VUS) : 10,
  duration: __ENV.DURATION ? __ENV.DURATION : '30s',
};

export default function () {
  // Αιτούμενο αρχείο από το περιβάλλον.
  let file = __ENV.FILE;
  let url = `http://nginx:8080/api/v1/files/${file}`;

  let res = http.get(url);

  // Έλεγχος αν το request ολοκληρώθηκε με επιτυχία.
  check(res, { 'status was 200': (r) => r.status === 200 });

  sleep(1);
}
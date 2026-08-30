import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    // Define the number of iterations for the test
    iterations: 10,
};

export default function () {
    http.get('http://localhost:8080/api/posts');
    sleep(1);
    http.get('http://localhost:8080/api/categories');
    sleep(1);
}
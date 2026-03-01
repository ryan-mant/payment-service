import http from 'k6/http';
import { check, sleep } from 'k6';
// Importamos o gerador de UUID do próprio k6
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    vus: 200,
    duration: '30s',
};

export default function () {
    const url = 'http://host.docker.internal:8080/api/v1/transfers';

    // Agora CADA requisição é uma transação totalmente nova e exclusiva!
    // Gera um valor aleatório entre 1.00 e 80.00 com duas casas decimais
    const randomAmount = parseFloat((Math.random() * (80 - 1) + 1).toFixed(2));

    const payload = JSON.stringify({
        id: uuidv4(),
        payerId: '67949e36-db0d-4609-bb55-ecac946dd67e',
        payeeId: 'cb9d4b4c-5699-492d-af26-a5d164b6312a',
        amount: randomAmount
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    const res = http.post(url, payload, params);

    // Verifica se o Spring Boot devolveu Sucesso
    check(res, {
        'transacao aprovada': (r) => r.status === 200 || r.status === 201,
    });

    // sleep(0.1);
}
import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    vus: 200,
    duration: '30s',
};

const payers = [
    '62abbb9f-5a9f-4e52-98c6-b591bf726d6e',
    '4fc37042-3868-4e6b-b948-df7aaccd80b3',
    '0148056a-44c5-42ef-a9a9-a1c12b3995aa',
    '29b6558a-c6f0-48cd-be73-8298a6ade16e',
    '2d1352d1-0885-4249-bcae-33947c424a1e',
    'e7a0137f-c89a-4ec9-aa14-9839c60b8ad5',
    '9120b1ee-b44f-439d-95ed-80f5135db579',
    '29155aa7-a755-480d-87b5-db8e6ec8855f',
    '8468339f-baa2-4dc5-b77e-9ec901e37d2d',
    'ef82d98c-217c-4fb9-8c6d-8e4354a66323',
    '0f7f1b9e-e650-4ae0-9300-f4c7e946ffa6',
    'd109da63-a58c-447e-9d07-cab0d5e5359a',
    'a12261f0-30a1-48c5-b622-33ca09a3ea54',
    '873b2d09-6b6a-4b56-936a-a492b7be998e',
    'a3c9cb6e-a924-4d7b-b902-8504aa217847',
    'a38cdc7b-9099-48da-98c5-83042efa89dc',
    '348d3b1a-d58c-4ce7-adde-c99e38b0c874',
    '6c433fc3-7c73-459f-b584-c3498587bb95',
    'f385fa3b-b2b9-44ff-8e23-38f4523b21d6',
    '755486c6-b4c4-4e2c-8fb2-1584805a7fb1',
    'ba609b7e-1931-496a-a707-2b03d533acdf',
    '9b651607-c822-4cc0-bbb9-7b458ed0da95',
    'c39ae3f6-c391-4e3f-bfc9-5abbd4b6b6ca',
    '283f7b6d-89bf-4fb9-ac14-9691f1320151',
    '06a03617-7ed5-4d20-b3c2-3ab70205db32',
    '4de1a39a-3d9d-4c5b-a22d-4a7ccdad0e79',
    '9f526c4f-ea7e-421e-bab9-4170b3ebf1b1',
    '64c01068-9fe3-463e-a5e8-804c0ec495fc',
    'a982f6d7-c2be-4459-afee-a715b42f2c1f',
    '59c4da8a-4dcb-436f-b932-469ef32e8a84',
    '270fde91-8ce5-42c9-8f03-877631e7e7bc',
    '0b66d65a-3701-4c68-8db1-3b47e06c34e0',
    'b06c8db4-acd0-4952-ab6e-7d194454be93',
    '59e7054d-d20a-402b-985a-b06d4d818e01',
    '17c0e74d-9270-4da8-ad0f-54c414afde5e',
    '73693d7e-4772-4937-aec5-37d2aedc23b2',
    'ae8b81a8-6015-44c8-87e4-a79d9142c1b8',
    '7e602288-5c46-4115-8152-5ea4030c008b',
    '0765c597-f782-4c02-8eb0-a705da66e440',
    '317cadc9-afbf-45a5-b1df-79455e01bf47',
    'c9bde772-6f4f-45ef-926b-c27b064e8116',
    'd20d841b-4814-4e06-8827-174634785cb0',
    '4ab7c121-2397-41d3-bd39-4074d94d48f6',
    'b4ace770-1ad6-4434-9ede-0a0ab1e7572c',
    'c67dde6b-10ab-4d2c-b1f7-1baf6a144b3b',
    'e7f5d4b6-0ccf-42d8-ab5c-7da6f9c6a110',
    'f3ecbae3-fbf4-442e-9279-3946e19305c9',
    '2aebfdcb-b227-4d53-b21f-45d24bf6b3a6',
    '72fdbcc6-c5de-4dc3-aaac-5f5d973fad20',
    '3dc5f475-6d02-44c2-82cb-921b63c87294'
];

const payees = [
    '87ef7084-642a-4e28-bc2e-6a14f2a2f310',
    '617c8181-3683-454d-aacc-725f08db69a5',
    'ba536ecb-025a-4104-b531-962c19b5d7e9',
    'af5bb540-ef8c-46a5-b7a9-e71f3d8386a6',
    'a1c410e3-b752-4fad-a439-e93a45d0a047',
    'f8f9d2ae-4ff0-4eff-bf56-76768809c623',
    '970bd9a8-06b8-4916-b6fe-1b7f8908e598',
    '625e0775-9bff-457c-9af8-313c34709c09',
    'e7cdeca0-4eee-4c52-8225-51ef7b25c233',
    'bb1158e5-8567-4570-99c5-def4082a04fc',
    '0cc22b5b-2585-466b-9982-f96787ee4643',
    '2185ff9f-3da8-4cbf-a7b9-3a4f593e988e',
    '2c7ab4ed-7d4c-4e44-9697-72eb1fd8db34',
    '72f1eb14-e29b-4e5e-bc73-f025d437f406',
    'bc59edf6-bd55-42a6-bbd4-1a615541192b',
    '42d64c89-bd1f-457e-900e-b377f0799fda',
    '8375db51-c6b8-4018-85ec-8327c843342b',
    '0c78cba9-ecfd-4bca-94ef-c06622486e2f',
    'ab79f762-e688-4b9a-afbc-6d43896ed61f',
    'b60f8023-dedf-4e44-bed3-66420ff261f1',
    '1ba76f91-0cf0-457d-8be2-c19297b2d557',
    '185830e4-6735-4d90-b0c6-c1cd760be698',
    '20428256-c978-49b8-96f6-812916067910',
    '08750c91-1173-4ddb-9fd2-a983480015d7',
    '06e68da6-9aab-4fe1-94d4-63867b33e050',
    'db696cc0-4865-40f2-82db-77a74a10da95',
    'ef6d327d-06d7-4790-bd71-ca0aef675475',
    '4100573d-14ba-4da3-95eb-f1c6ff1331b2',
    '0a1ee8f0-fb6f-4c33-baba-6c6a112d3843',
    '6290d844-d62b-4ab9-8e86-e53ad707ac02',
    '65d5a5b2-a772-4c0e-884d-c6056da467ae',
    'fba05ff9-0bc5-40c1-b761-2cb6e3aaa059',
    '0dd8ce20-7bb9-4eec-b22a-3ad8215c57e7',
    '41ecb404-873b-47e6-b99c-46c64bf12b46',
    'e637c9be-fb9f-4f9e-b78c-5bf7c6fda13b',
    '2fbc13f4-58b7-4e27-b110-97e00c960bd5',
    'fa45ebf1-bbc2-496c-af50-43b28b3008ca',
    'c2055833-45e6-4c3b-99fc-c485c2a0c730',
    '412592ca-aef6-4dd5-82b2-2324f6f7a779',
    '2eea160c-8231-4824-b74a-087d3f5b5e14',
    '8bbb7b66-7f3e-4856-8235-30ea6f8314b2',
    '0e2a6221-9223-47a9-baf9-6d1081afddba',
    '113ed586-0557-4f37-87c0-bdb56b53495c',
    'dadfc013-633d-469e-a57f-65637cd06331',
    'bee74853-3faa-4326-bbb2-8dfd2a402dfb',
    '48f5a6fb-9885-4b11-b851-202e98c72af2',
    '564406dc-18f2-4b23-9a31-8468c2edd36d',
    '5f994b9f-4e1e-4427-b610-84990d80360f',
    'e73837d8-b12f-49df-911a-d9c18325dc40',
    'f0bec093-d2f5-4f27-8b09-dfed8ca2c247'
];

export default function () {
    const url = 'http://localhost:8080/api/v1/transfers';

    const payerId = payers[Math.floor(Math.random() * payers.length)];
    const payeeId = payees[Math.floor(Math.random() * payees.length)];

    const randomAmount = parseFloat((Math.random() * (80 - 1) + 1).toFixed(2));

    const payload = JSON.stringify({
        id: uuidv4(),
        payerId: payerId,
        payeeId: payeeId,
        amount: randomAmount
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'transacao aprovada': (r) => r.status === 200 || r.status === 201,
    });
}
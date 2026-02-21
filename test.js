import http from 'k6/http';
import {check, sleep} from 'k6';

// 테스트 설정
export const options = {
    stages: [
        {duration: '30s', target: 10}, // 30초 동안 사용자 10명까지 점진적 증가
        {duration: '1m', target: 30},  // 1분 동안 사용자 30명 유지 (Peak)
        {duration: '20s', target: 0},  // 20초 동안 종료
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청은 500ms 이내에 완료되어야 함
    },
};

// 기본 설정
const BASE_URL = 'http://localhost:8081'; // Docker 사용 시

export default function () {
    // 1. 목록 조회 (검색 조건 포함)
    // RentalPostSearchRequestDto 필드에 맞춰 쿼리 파라미터 구성
    const searchParams = 'lat=37.5665&lng=126.9780&distance=5.0';
    const listRes = http.get(`${BASE_URL}/products?${searchParams}`);

    check(listRes, {
        'list status is 200': (r) => r.status === 200,
    });

    // 2. 상세 조회 (랜덤하게 1~10번 게시글 중 하나 조회한다고 가정)
    // 실제 DB에 있는 ID 범위를 고려하여 수정하세요.
    const postId = Math.floor(Math.random() * 10) + 1;
    const detailRes = http.get(`${BASE_URL}/products/${postId}`);

    check(detailRes, {
        'detail status is 200': (r) => r.status === 200,
        'detail has body': (r) => r.body.length > 0,
    });

    // 3. 인기 게시글 조회
    const popularRes = http.get(`${BASE_URL}/products/popular`);
    check(popularRes, {
        'popular status is 200': (r) => r.status === 200,
    });

    sleep(1); // 사용자 행동 간의 간격 (1초)
}
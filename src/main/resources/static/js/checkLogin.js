function checkLogin() {
    // 데이터 속성에서 로그인 여부 확인
    const isLoggedIn = document.getElementById('loginStatus').dataset.isLoggedIn === 'true';

    if (!isLoggedIn) {
        // 로그인되지 않은 경우 경고창 표시 후 로그인 페이지로 이동
        alert('로그인 후 이용해주세요.');
        window.location.href = '/sogong/train/login'; // 로그인 페이지로 리다이렉트
    } else {
        // 로그인된 경우 폼 제출
        document.getElementById('seat-select-btn').submit();
    }
}
document.addEventListener("DOMContentLoaded", function () {
    const images = [
        "/img/train1.jpg", // 절대 경로로 변경
        "/img/train2.jpg",
        "/img/train3.jpg"
    ]; // 슬라이드 이미지 경로 배열

    let currentImageIndex = 0;

    // .header 요소를 가져오기
    const header = document.querySelector(".header");
    if (!header) {
        console.error("Error: .header 요소를 찾을 수 없습니다.");
        return;
    }

    function changeBackground() {
        // 이미지 변경
        header.style.backgroundImage = `url('${images[currentImageIndex]}')`;
        currentImageIndex = (currentImageIndex + 1) % images.length; // 다음 이미지로 전환
    }

    // 3초마다 배경 이미지 변경
    setInterval(changeBackground, 3000);

    // 초기 배경 이미지 설정
    changeBackground();
});

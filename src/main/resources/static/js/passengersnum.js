// 승객 수 조정 js
document.getElementById('passenger-decrease').addEventListener('click', () => {
    const passengerInput = document.getElementById('passenger');
    let passengerCount = parseInt(passengerInput.value, 10);

    if (passengerCount > 1) { // 최소 1명 이상
        passengerCount -= 1;
        passengerInput.value = passengerCount;
    }
});

document.getElementById('passenger-increase').addEventListener('click', () => {
    const passengerInput = document.getElementById('passenger');
    let passengerCount = parseInt(passengerInput.value, 10);

    if (passengerCount < 10 && passengerCount > 0) { // 최대 10명까지 제한
        passengerCount += 1;
        passengerInput.value = passengerCount;
    }
});
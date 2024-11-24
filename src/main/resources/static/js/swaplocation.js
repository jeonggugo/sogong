function swapLocations() {
    const departureInput = document.getElementById('departure');
    const arrivalInput = document.getElementById('arrival');

    // 출발지와 도착지 값 교환
    const temp = departureInput.value;
    departureInput.value = arrivalInput.value;
    arrivalInput.value = temp;
}
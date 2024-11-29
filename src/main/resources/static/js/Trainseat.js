function selectCarriage(carriageNumber) {
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelector(`.tab:nth-child(${carriageNumber})`).classList.add('active');
    generateSeats(carriageNumber);
}

function generateSeats(carriageNumber) {
    const seatMap = document.getElementById('seat-map');
    seatMap.innerHTML = '';
    const seatData = getSeatData(carriageNumber);
    seatData.forEach(seatId => {
        const seat = document.createElement('div');
        seat.classList.add('seat');
        seat.textContent = seatId;
        seat.dataset.seatId = seatId;

        if (isSeatUnavailable(carriageNumber, seatId)) {
            seat.classList.add('unavailable');
        } else {
            seat.addEventListener('click', () => toggleSeat(seat, carriageNumber));
        }

        seatMap.appendChild(seat);
    });
}

function toggleSeat(seat, carriageNumber) {
    const selectedSeats = document.querySelectorAll('.seat.selected');

    if (seat.classList.contains('selected')) {
        seat.classList.remove('selected');
    } else if (selectedSeats.length < passengerCount) {
        seat.classList.add('selected');
    } else {
        alert(`최대 ${passengerCount}개의 좌석만 선택할 수 있습니다.`);
    }

    updateSelectedSeats(carriageNumber);
}

function updateSelectedSeats(carriageNumber) {
    const selectedSeats = Array.from(document.querySelectorAll('.seat.selected')).map(seat => seat.dataset.seatId);
    document.getElementById('selected-seats-list').textContent = selectedSeats.join(', ') || '없음';
    document.getElementById('submitBtn').disabled = selectedSeats.length === 0;

    // Save the selected seats to localStorage
    saveSelectedSeats(carriageNumber, selectedSeats);
}

function submitSeats() {
    // 선택된 좌석 가져오기
    const selectedSeats = Array.from(document.querySelectorAll('.seat.selected')).map(seat => seat.dataset.seatId);
    if (selectedSeats.length === 0) {
        alert('좌석을 선택해주세요.');
        return; // 선택된 좌석이 없는 경우 폼 제출을 중단합니다.
    }

    // 선택된 좌석 정보를 숨겨진 필드에 설정
    const selectedSeatsInput = document.getElementById('selectedSeatsInput');
    selectedSeatsInput.value = selectedSeats.join(',');

    // 선택된 좌석을 알림
    alert('선택된 좌석: ' + selectedSeats.join(', '));

    // 선택된 좌석을 마크하고 저장
    const carriageNumber = getActiveCarriage();
    saveUnavailableSeats(carriageNumber, selectedSeats);

    // 폼을 명확하게 선택해서 제출
    const form = document.querySelector('form');
    form.submit();
}

function saveSelectedSeats(carriageNumber, selectedSeats) {
    localStorage.setItem(`selectedSeats_carriage_${carriageNumber}`, JSON.stringify(selectedSeats));
}

function getSeatData(carriageNumber) {
    return ['1A', '1B', '1C', '1D', '2A', '2B', '2C', '2D', '3A', '3B', '3C', '3D', '4A', '4B', '4C', '4D']; // Example seat data
}

function isSeatUnavailable(carriageNumber, seatId) {
    const unavailableSeats = JSON.parse(localStorage.getItem(`unavailableSeats_carriage_${carriageNumber}`)) || [];
    return unavailableSeats.includes(seatId);
}

function saveUnavailableSeats(carriageNumber, seats) {
    let unavailableSeats = JSON.parse(localStorage.getItem(`unavailableSeats_carriage_${carriageNumber}`)) || [];
    unavailableSeats = [...new Set([...unavailableSeats, ...seats])]; // Merge without duplicates
    localStorage.setItem(`unavailableSeats_carriage_${carriageNumber}`, JSON.stringify(unavailableSeats));
}

function getActiveCarriage() {
    return Array.from(document.querySelectorAll('.tab')).findIndex(tab => tab.classList.contains('active')) + 1;
}
function goToPreviousPage() {
    window.history.back();
}


// 초기화
selectCarriage(1);

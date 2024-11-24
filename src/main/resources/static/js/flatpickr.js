flatpickr("#departure-time", {
    enableTime: false, // 시간 선택 불가능
    dateFormat: "Y-m-d", // 날짜 및 시간 형식
    minDate: "today", // 오늘 이후 날짜만 선택 가능
    defaultDate: new Date(), // 기본 값을 오늘로 설정
    locale: "ko" // 한국어 설정
});

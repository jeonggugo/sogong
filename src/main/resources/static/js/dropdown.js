// 드롭다운 메뉴 토글
function toggleDropdown(dropdownId) {
    const dropdown = document.getElementById(dropdownId);
    dropdown.classList.toggle('active');
}

// 도시 선택 시 값 변경
function selectCity(inputId, city) {
    const input = document.getElementById(inputId);
    input.value = city;

    // 드롭다운 닫기
    const dropdown = document.getElementById(inputId + '-dropdown');
    dropdown.classList.remove('active');
}

// 페이지 외부 클릭 시 드롭다운 닫기
document.addEventListener('click', (event) => {
    const dropdowns = document.querySelectorAll('.dropdown-menu');
    dropdowns.forEach((dropdown) => {
        if (!dropdown.contains(event.target) && !dropdown.previousElementSibling.contains(event.target)) {
            dropdown.classList.remove('active');
        }
    });
});
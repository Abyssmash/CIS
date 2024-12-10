document.addEventListener('DOMContentLoaded', () => {
    const taskLinks = document.querySelectorAll('.task-link'); // 업무 제목 링크
    const taskDetailSection = document.querySelector('#task-detail'); // 상세보기 섹션
    const loadingIndicator = document.querySelector('#loading'); // 로딩 인디케이터

    taskLinks.forEach(link => {
        link.addEventListener('click', event => {
            event.preventDefault(); // 기본 링크 동작 차단
            const taskId = link.dataset.taskId; // 업무 번호 가져오기
            loadTaskDetails(taskId);
        });
    });

    // 업무 상세보기 로드 함수
    function loadTaskDetails(taskId) {
        // 로딩 인디케이터 표시
        loadingIndicator.style.display = 'block';

        const xhr = new XMLHttpRequest();
        xhr.open('GET', `/tasks/details/${taskId}`, true);
        xhr.onload = function () {
            // 로딩 인디케이터 숨기기
            loadingIndicator.style.display = 'none';

            if (xhr.status === 200) {
                taskDetailSection.innerHTML = xhr.responseText; // 서버에서 받은 HTML로 채우기
                taskDetailSection.style.display = 'block'; // 상세보기 섹션 보이기
            } else if (xhr.status === 404) {
                alert('해당 업무를 찾을 수 없습니다.');
            } else {
                alert('업무 상세 정보를 불러오는 데 실패했습니다.');
            }
        };
        xhr.onerror = function () {
            // 로딩 인디케이터 숨기기
            loadingIndicator.style.display = 'none';
            alert('요청 중 문제가 발생했습니다.');
        };
        xhr.send();
    }

    // 닫기 버튼 이벤트
    document.querySelector('#close-detail-btn').addEventListener('click', () => {
        taskDetailSection.style.display = 'none'; // 상세보기 숨기기
    });
});

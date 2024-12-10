document.addEventListener('DOMContentLoaded', () => {
    const taskForm = document.querySelector('#task-form');
    const submitButton = taskForm.querySelector('button[type="submit"]');
    const loadingIndicator = document.querySelector('#loading-indicator'); // 로딩 인디케이터

    // 업무 보내기 후 받은 사람의 업무 리스트 자동 갱신
    function sendTask(event) {
        event.preventDefault(); // 폼 기본 동작 방지

        // 버튼 비활성화, 로딩 표시
        submitButton.disabled = true;
        loadingIndicator.style.display = 'block';

        const formData = new FormData(taskForm); // 폼 데이터 가져오기

        // 업무 전송 로직 (fetch 사용)
        fetch('/tasks/send', {
            method: 'POST',
            body: formData, // 폼 데이터를 서버로 전송
        })
            .then(response => {
                if (response.ok) {
                    // 업무 전송이 완료되면 받은 사람의 업무 목록을 최신화
                    return fetch('/tasks/received');
                }
                throw new Error('업무 전송에 실패했습니다.');
            })
            .then(response => response.text())
            .then(data => {
                // 받은 업무 목록을 업데이트
                document.querySelector('#tasks-list').innerHTML = data;

                // 폼 초기화
                taskForm.reset();
            })
            .catch(error => {
                console.error(error);
                alert('업무 전송에 오류가 발생했습니다.');
            })
            .finally(() => {
                // 버튼 활성화, 로딩 숨기기
                submitButton.disabled = false;
                loadingIndicator.style.display = 'none';
            });
    }

    // 폼 제출 시 sendTask 함수 실행
    taskForm.addEventListener('submit', sendTask);
});

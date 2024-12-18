document.addEventListener('DOMContentLoaded', () => {
    const taskForm = document.querySelector('#task-form');
    const submitButton = taskForm.querySelector('.send_btn');
    const loadingIndicator = document.querySelector('#loading-indicator'); // 로딩 인디케이터 (필요한 경우 추가)
    const receiveIdError = document.querySelector('#receive-id-error');
    const tasksList = document.querySelector('#tasks-list');

    // 업무 보내기 후 받은 사람의 업무 리스트 자동 갱신
    async function sendTask(event) {
        event.preventDefault(); // 폼 기본 동작 방지

        // 폼 데이터에서 받은 사람 ID 가져오기
        const receiveId = document.querySelector('#receive_id').value;
        console.log("Received ID (before sending):", receiveId);  // 확인용 로그 추가

        if (!receiveId) {
            showErrorMessage('받는 사람 ID를 입력해주세요.');
            return;
        }

        // FormData 생성
        const formData = new FormData(taskForm);

        // FormData 내용 출력
        formData.forEach((value, key) => {
            console.log(key, value); // key와 value 출력
        });

        // 받은 사람 ID를 FormData에 추가 (여기서 receive_id로 추가)
        formData.append('receive_id', receiveId);

        // 버튼 비활성화 및 로딩 표시
        toggleLoadingState(true);

        try {
            // 받은 사람 ID 유효성 검사
            const isValid = await checkReceiveId(receiveId);
            if (!isValid) {
                showErrorMessage('유효하지 않은 ID입니다.');
                toggleLoadingState(false);
                return;
            }

            // 받은 사람 ID가 유효한 경우
            clearErrorMessage();

            // 업무 전송
            const sendResponse = await fetch('/tasks/send', {
                method: 'POST',
                body: formData,
            });

            if (!sendResponse.ok) {
                throw new Error('업무 전송에 실패했습니다.');
            }

            // 업무 전송 성공 시 받은 업무 목록 갱신
            const tasksResponse = await fetch('/tasks/received');
            if (tasksResponse.ok) {
                const tasksHTML = await tasksResponse.text();
                tasksList.innerHTML = tasksHTML;
            }

            // 폼 초기화
            taskForm.reset();
        } catch (error) {
            console.error(error);
            alert('업무 전송에 오류가 발생했습니다.');
        } finally {
            // 버튼 활성화 및 로딩 숨기기
            toggleLoadingState(false);
        }
    }

    // 받은 사람 ID 유효성 검사 (서버 요청)
    async function checkReceiveId(receiveId) {
        console.log("Checking ID:", receiveId);  // 유효성 검사 전 ID 로그 추가
        try {
            const response = await fetch(`/tasks/checkReceiveId?receiveId=${encodeURIComponent(receiveId)}`);
            if (!response.ok) {
                throw new Error('유효성 검사 중 오류가 발생했습니다.');
            }
            const data = await response.json();
            console.log("Server response:", data);  // 서버 응답 내용 확인
            return data.isValid; // 서버에서 유효성 검사 결과 반환
        } catch (error) {
            console.error(error);
            return false; // 오류 발생 시 유효하지 않은 것으로 간주
        }
    }

    // 버튼 비활성화/활성화 및 로딩 상태 변경
    function toggleLoadingState(isLoading) {
        submitButton.disabled = isLoading;
        if (loadingIndicator) {
            loadingIndicator.style.display = isLoading ? 'block' : 'none';
        }
    }

    // 오류 메시지 표시
    function showErrorMessage(message) {
        if (receiveIdError) {
            receiveIdError.textContent = message;
        }
    }

    // 오류 메시지 초기화
    function clearErrorMessage() {
        if (receiveIdError) {
            receiveIdError.textContent = '';
        }
    }

    // 폼 제출 시 sendTask 함수 실행
    taskForm.addEventListener('submit', sendTask);

    const receiveId = String(document.querySelector('#receive_id').value);
    console.log("Received ID (before sending):", receiveId); // 확인용 로그
});

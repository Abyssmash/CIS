document.addEventListener('DOMContentLoaded', () => {
    const tasklist = document.getElementById("taskContent");
    const bintasklist = document.getElementById("taskContentEmpty");
    const loadingIndicator = document.getElementById('loading'); // 로딩 인디케이터
    const taskDetailSection = document.querySelector('#task-detail-container');

    // 업무 완료 버튼 클릭 처리
    const completeButtons = document.querySelectorAll('.complete-btn');
    completeButtons.forEach(button => {
        button.addEventListener('click', event => {
            const taskNum = event.target.dataset.taskNum;
            markTaskAsComplete(taskNum);
        });
    });

    // 업무 상태 변경 (완료 처리)
    function markTaskAsComplete(taskNum) {
        fetch(`/tasks/${taskNum}/complete`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content') // CSRF 보호
            }
        })
            .then(response => {
                if (response.ok) {
                    alert('업무가 완료 처리되었습니다.');
                    loadTasks(); // 완료 후 업무 목록 갱신
                } else {
                    alert('업무 완료 처리 중 오류가 발생했습니다.');
                }
            })
            .catch(error => console.error('Error:', error));
    }

    // 페이징 처리
    const paginationLinks = document.querySelectorAll('.pagination-link');
    paginationLinks.forEach(link => {
        link.addEventListener('click', event => {
            event.preventDefault();
            const page = event.target.dataset.page;
            const status = event.target.dataset.status || ''; // 상태 필터링 값
            loadTasks(status, page);
        });
    });

    // 개인 업무 목록 로드
    function loadTasks(status = '', page = 1) {
        loadingIndicator.style.display = 'block'; // 로딩 표시

        fetch(`/tasks?status=${status}&page=${page}`)
            .then(response => response.text())
            .then(html => {
                document.querySelector('#task-list-container').innerHTML = html;

                // 업무가 없으면 빈 목록 표시
                if (!html.trim()) {
                    bintasklist.style.display = 'block';
                    tasklist.style.display = 'none';
                } else {
                    bintasklist.style.display = 'none';
                    tasklist.style.display = 'block';
                }
            })
            .catch(error => console.error('Error:', error))
            .finally(() => {
                loadingIndicator.style.display = 'none'; // 로딩 숨기기
            });
    }

    // 업무 상세 정보 보기
    const taskLinks = document.querySelectorAll('.task-link');
    taskLinks.forEach(link => {
        link.addEventListener('click', event => {
            event.preventDefault();
            const taskId = event.target.dataset.taskNum;
            viewTaskDetails(taskId);
        });
    });

    // 업무 상세 정보 보기
    function viewTaskDetails(taskNum) {
        loadingIndicator.style.display = 'block'; // 로딩 표시

        fetch(`/tasks/${taskNum}`)
            .then(response => response.text())
            .then(html => {
                taskDetailSection.innerHTML = html;
                document.querySelector('#task-detail-modal').classList.add('visible'); // 모달 열기
            })
            .catch(error => console.error('Error:', error))
            .finally(() => {
                loadingIndicator.style.display = 'none'; // 로딩 숨기기
            });
    }

    // 모달 닫기
    const modalCloseButton = document.querySelector('#modal-close-btn');
    if (modalCloseButton) {
        modalCloseButton.addEventListener('click', () => {
            document.querySelector('#task-detail-modal').classList.remove('visible'); // 모달 닫기
        });
    }

    // 파일 업로드 갯수 제한
    document.querySelector("form").addEventListener("submit", function (e) {
        var files = document.querySelectorAll('input[type="file"]');
        var fileCount = 0;

        // 파일 선택 갯수 체크
        files.forEach(function (input) {
            if (input.files.length > 0) {
                fileCount += input.files.length;
            }
        });

        if (fileCount > 3) {
            e.preventDefault(); // 폼 제출을 막음
            alert("업로드할 수 있는 파일은 최대 3개입니다.");
        }
    });
});

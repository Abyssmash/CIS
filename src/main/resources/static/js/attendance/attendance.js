let attendance_row = document.getElementById("attendance_list");
let attendance_empty = document.getElementById("attendance_empty");
let commute_container = document.getElementById("commute_container");

let today = new Date();
let year = today.getFullYear();
let month = String(today.getMonth() + 1).padStart(2,'0');
let day = String(today.getDate()).padStart(2,'0');
let now_date = (year + "-" + month + "-" + day);

let work_start = document.getElementsByClassName("work_start")[0];
let work_end = document.getElementsByClassName("work_end")[0];
let significant_btn = document.getElementsByClassName("significant_btn")[0];
let significant_select_box = document.getElementById("significant");

let check_val = false;
let work_start_check_val = 0;
let work_end_check_val = 0;

window.onload = () => {
    if (attendance_row.rows.length <= 0) {
        attendance_empty.style.display = "";
        work_end.style.display = "none";
    } else {
        attendance_empty.style.display = "none";
    }

    if (attendanceWorkStartCheck() > 0) {
        if (!(attendanceWorkEndCheck() > 0)) {
            attendanceHiddenElement();
        } else {
            work_start.style.display = "none";
        }
    } else {
        work_end.style.display = "none";
        significant_select_box.disabled = "disabled";
        significant_btn.disabled = "disabled";
    }
}

function attendanceWorkStartCheck() {
    let result_check = 0;
    $.ajax({
        type : "GET",
        url : "/attendance/work_start/check",
        async : false,
        data : {
            "now_date" : now_date
        },
        success : function(check) {
            if (check > 0) {
                work_start_check_val = check;
                result_check = check;
            }
        },
        error : function() {
            alert("서버 요청 실패");
        }
    });
    return result_check;
}

function attendanceWorkEndCheck() {
    let result_check = 0;
    $.ajax({
        type : "GET",
        url : "/attendance/work_end/check",
        async : false,
        data : {
            "now_date" : now_date
        },
        success : function(check) {
            if (check > 0) {
                work_end_check_val = check;
                result_check = check;
            }
        },
        error : function() {
            alert("서버 요청 실패");
        }
    });
    return result_check;
}

function attendanceHiddenElement() {
    work_end.style.display = "none";

    const text = document.createElement("p");
    text.style.fontSize = "40px";
    text.style.fontWeight = "bold";
    text.innerText = "수고하셨습니다.";

    commute_container.appendChild(text);

    significant_select_box.disabled = "disabled";
    significant_btn.disabled = "disabled";
    significant_btn.style.display = "none";

    work_start.style.display = "none";
}

work_start.addEventListener("click", () => {
    if (attendance_row.rows.length > 0) {
        if (work_start_check_val > 0) {
            alert("이미 출근 처리 되었습니다.\n출근 내역을 확인해주세요.");
            return;
        }
    }

    work_start.style.display = "none";
    work_end.style.display = "";
    significant_select_box.disabled = "enabled";
    significant_btn.disabled = "enabled";

    const form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "go_to_work");

    let hour = today.getHours();
    let minute = today.getMinutes();
    let late_check = "N";

    if (hour >= 9 && (hour > 9 || minute > 30)) late_check = "Y";

    const late_check_input = document.createElement("input");
    late_check_input.setAttribute("type", "hidden");
    late_check_input.setAttribute("name", "late_check");
    late_check_input.setAttribute("value", late_check);

    form.appendChild(late_check_input);
    document.body.appendChild(form);
    form.submit();
});

function leaveWork(significant_value) {
    const form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", "leave_work");

    const work_date_input = document.createElement("input");
    work_date_input.setAttribute("type", "hidden");
    work_date_input.setAttribute("name", "work_date");
    work_date_input.setAttribute("value", now_date);

    let end_hour = today.getHours();
    let end_minute = today.getMinutes();
    let work_end_time = end_hour + ":" + end_minute;

    const work_end_input = document.createElement("input");
    work_end_input.setAttribute("type", "hidden");
    work_end_input.setAttribute("name", "work_end");
    work_end_input.setAttribute("value", work_end_time);

    const significant_input = document.createElement("input");
    significant_input.setAttribute("type", "hidden");
    significant_input.setAttribute("name", "significant");
    significant_input.setAttribute("value", significant_value);

    let work_start = attendance_row.rows[0].cells[1].innerText;
    let start_hour = parseInt(work_start.substring(0, 2));
    let start_minute = parseInt(work_start.substring(3, 5));
    let work_total_time = ((end_hour - start_hour) * 60) + (end_minute - start_minute);

    const work_total_input = document.createElement("input");
    work_total_input.setAttribute("type", "hidden");
    work_total_input.setAttribute("name", "work_total");
    work_total_input.setAttribute("value", work_total_time + "");

    form.appendChild(work_date_input);
    form.appendChild(work_end_input);
    form.appendChild(significant_input);
    form.appendChild(work_total_input);
    document.body.appendChild(form);

    form.submit();
}

function work_end_check() {
    if (attendance_row.rows.length > 0) {
        let check_date = attendance_row.rows[0].cells[0].innerText;
        let check_end_time = attendance_row.rows[0].cells[2].innerText;
        if (!(work_start_check_val > 0)) {
            alert("금일 출근 내역이 존재하지 않습니다.");
            return check_val = true;
        }
        if (!(work_end_check_val > 0) && !(check_end_time === "-")) {
            alert("이미 퇴근 처리 되었습니다.\n퇴근 내역을 확인해주세요.");
            return check_val = true;
        }
    } else {
        alert("금일 출근 내역이 존재하지 않습니다.");
        return check_val = true;
    }
}

work_end.addEventListener("click", () => {
    work_end_check();

    if (check_val) return;

    let significant_value = "퇴근";

    if (!(confirm("정말 사유 없이 퇴근합니까?"))) return;

    leaveWork(significant_value);
});

significant_btn.addEventListener("click", () => {
    work_end_check();

    if (check_val) return;

    let significant = document.getElementById("significant");
    let significant_value = significant.selectedOptions[0].innerText;

    if (!(confirm("사유 : " + significant_value))) return;

    leaveWork(significant_value);
});

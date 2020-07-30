const http = new XMLHttpRequest();

function drop(x) {
    http.open('GET', 'drop?' + 'id=' + x);
    http.send();
    http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById(this.responseText.split(",")[0]).style.backgroundColor = this.responseText.split(",")[1];
        }
    }
}

function computerTurn() {
    http.open('GET', 'computerTurn?');
    http.send();
    http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById(this.responseText.split(",")[0]).style.backgroundColor = this.responseText.split(",")[1];
            document.getElementById("time").innerText = "Time is: " + this.responseText.split(",")[2] + " ms";
        }
    }
}

function undoTurn() {
    http.open('GET', 'undoTurn?' + 'id=' + 'undoTurn');
    http.send();
    http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById(this.responseText).style.backgroundColor = "#eee169";
        }
    }
}

function test(x) {
    http.open('GET', 'test?' + 'id=' + x);
    http.send();
}

function clearAll() {
    http.open('GET', 'clearAll?' + 'id=' + 'clearAll');
    http.send();
    http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            for (let i = 0; i <= 47; i++) {
                if (i == 6 || i == 13 || i == 20 || i == 27 || i == 34 || i == 41)
                    continue
                document.getElementById(i.toString()).style.backgroundColor = "#eee169";
            }
        }
    }
}

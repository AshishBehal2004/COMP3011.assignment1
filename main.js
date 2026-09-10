const startButton = document.getElementById("start-button");
const stopButton = document.getElementById("stop-button");

let stream ;
let mediaRecorder;

startButton.addEventListener("click", async () =>{
	stream = await navigator.mediaDevices.getUserMedia({audio: true})
	mediaRecorder = new MediaRecorder(stream)
})

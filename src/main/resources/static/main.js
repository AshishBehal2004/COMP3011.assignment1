const startButton = document.getElementById("start-button");
const stopButton = document.getElementById("stop-button");
const audioPlayback = document.getElementById("audio-playback");

let stream ;
let mediaRecorder;
let audioChunks=[];
startButton.addEventListener("click", async () =>{
	stream = await navigator.mediaDevices.getUserMedia({audio: true})
	mediaRecorder = new MediaRecorder(stream) // records the audio
	mediaRecorder.addEventListener("dataavailable", (event) => { //listener which runs when the data is available as soon as possible
		audioChunks.push(event.data); // addding the record audio chunks to the array
	})
	
	mediaRecorder.addEventListener("stop", () => { 
		const blob = new Blob(audioChunks, {type: "audio/webm; codecs=opus"});
		const audioURL = window.URL.createObjectURL(blob);
		audioPlayback.src = audioURL;
		const formData = new FormData();
		formData.append("audio", blob);
		
		fetch("/api/v1/transcribe", {method: "POST", body: formData})
			.then(response => response.text())
			.then(text => console.log(text))
			
		
		
				
	})

	mediaRecorder.start();
})

	stopButton.addEventListener("click", () => {
	
		mediaRecorder.stop();
})
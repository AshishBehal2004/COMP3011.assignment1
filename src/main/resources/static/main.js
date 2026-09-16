const startButton = document.getElementById("start-button");
const stopButton = document.getElementById("stop-button");
const audioPlayback = document.getElementById("audio-playback");
const transcriptOutput = document.getElementById("transcript-output");
const recordingStatus = document.getElementById("recording-status");

let stream ;
let audioContext;
let sourceNode;
let processorNode;
let recordedChunks = [];

startButton.addEventListener("click", async () =>{
	
	try{
		stream = await navigator.mediaDevices.getUserMedia({audio: true});
			
			recordingStatus.textContent = "Recording...";
			audioContext = new AudioContext();
			sourceNode = audioContext.createMediaStreamSource(stream);
			
			processorNode = audioContext.createScriptProcessor(4096, 1, 1);
			
			processorNode.onaudioprocess = (event) => {
				const inputData = event.inputBuffer.getChannelData(0);
				recordedChunks.push(new Float32Array(inputData));
			}
			
			sourceNode.connect(processorNode);
			processorNode.connect(audioContext.destination);
	}
	catch(error){
		recordingStatus.textContent = "Microphone access denied: " + error.message;
	}
	

})

	stopButton.addEventListener("click", async () => {
		
		recordingStatus.textContent = "Not Recording..." ;
		processorNode.disconnect();
		sourceNode.disconnect();
		stream.getTracks().forEach(track => track.stop());
		
		const merged = mergeChunks(recordedChunks);
		const downsampled = downSampleTo16k(merged, audioContext.sampleRate, 16000);
		const pcmBlob = encodePcm16(downsampled);
		const formData = new FormData();
		
		formData.append("audio", pcmBlob, "recording.pcm");
		
		try {
			const response = await fetch("/api/v1/transcribe", { method: "POST", body: formData });
			if (!response.ok){
				throw new Error("Server returned status " + response.status);
			}
			const text = await response.text();
			transcriptOutput.textContent = text;
		}
		catch(error) {
			transcriptOutput.textContent = "Transcription failed: " + error.message;
		}
		
})

function mergeChunks(chunks){
	let totalLength = 0;
	for(const chunk of chunks){
		totalLength += chunk.length;
	}
	
	const result = new Float32Array(totalLength);
	let offset = 0;
	
	for(const chunk of chunks){
		result.set(chunk, offset);
		offset += chunk.length;
	}
	return result;
}

function downSampleTo16k(samples, inputRate, outputRate){
	const ratio = inputRate / outputRate;
	const newLength = Math.round(samples.length / ratio);
	const result = new Float32Array(newLength);
	
	for(let i= 0 ; i < newLength; i++){
		result[i] = samples[Math.round(i*ratio)];
	}
	return result;
}

function encodePcm16(samples){
	
	const buffer = new ArrayBuffer(samples.length * 2);
  	const view = new DataView(buffer);

  	let offset = 0;
  	for (let i = 0; i < samples.length; i++, offset += 2) {
      	const clamped = Math.max(-1, Math.min(1, samples[i]));
      	view.setInt16(offset, clamped < 0 ? clamped * 0x8000 : clamped * 0x7fff, true);
  	}
	return new Blob([view], { type: "application/octet-stream" });
}
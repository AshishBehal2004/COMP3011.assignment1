const startButton = document.getElementById("start-button");
const stopButton = document.getElementById("stop-button");
const audioPlayback = document.getElementById("audio-playback");

let stream ;
let audioContext;
let sourceNode;
let processorNode;
let recordedChunks = [];

startButton.addEventListener("click", async () =>{
	stream = await navigator.mediaDevices.getUserMedia({audio: true})
	
	audioContext = new AudioContext();
	sourceNode = audioContext.createMediaStreamSource(stream);
	
	processorNode = audioContext.createScriptProcessor(4096, 1, 1);
	
	processorNode.onaudioprocess = (event) => {
		const inputData = event.inputBuffer.getChannelData(0);
		recordedChunks.push(new Float32Array(inputData));
	}
	
	sourceNode.connect(processorNode);
	processorNode.connect(audioContext.destination);

})

	stopButton.addEventListener("click", () => {
		processorNode.disconnect();
		sourceNode.disconnect();
		stream.getTracks().forEach(track => track.stop());
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
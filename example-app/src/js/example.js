import { VlcRtspPlayer } from 'vlc-rtsp-player';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    VlcRtspPlayer.echo({ value: inputValue })
}

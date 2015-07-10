(
SynthDef("masterOut", {
	var sig = In.ar(~masterBus,2);
	sig = Limiter.ar(sig, 0.9);
	sig = FreeVerb2.ar(sig[0],sig[1], room:0.66, mix:0.4);
	Out.ar(0, sig);
}).add;

SynthDef(\wobbleGhost, {
	arg wobbleHz = 4, spread=0.125, freq=120, slideTime = 0.2, amp=0.6, gate=1;
	var sig1, sig2, wobbleSig, wobbleRate, sigOut, env;
	freq = Lag.kr(freq, slideTime);
	amp = Lag.kr(amp, slideTime);
	wobbleHz = Lag.kr(wobbleHz, slideTime);
	wobbleRate = LFNoise2.kr(1!2).range(wobbleHz, wobbleHz * 1.5);
	wobbleSig = SinOsc.kr(wobbleRate, mul:spread * freq);
	sig1 = SinOsc.ar((freq * 0.98) + wobbleSig[0], mul:amp * 0.69);
	sig2 = SinOsc.ar((freq * 1.02) + wobbleSig[1], mul:amp * 0.69);
	sigOut = Splay.ar([sig1, sig2], spread:0.8);
	env = EnvGen.kr(Env.adsr(0.01, 0.12, 0.4, 4, 1, \sine), gate:gate, doneAction:2);
	sigOut = sigOut * env;
	Out.ar(~masterBus, sigOut);
}).add;

SynthDef(\smoothGhosts, {
	arg moveHz=4, loFreq=440, hiFreq=880, gate=1, amp=1.0;
	var freq, sig, sig2, env, ghostCount=22;
	freq = LFNoise2.kr(moveHz!ghostCount).exprange(loFreq, hiFreq);
	amp = LFNoise2.kr(moveHz!ghostCount).exprange(0.01, amp);
	//amp = amp / (ghostCount/2);
	sig = SinOsc.ar(freq) * amp;
	sig2 = Splay.ar(sig, spread:0.9);
	env = EnvGen.kr(Env.asr, gate:gate, doneAction:2);
	sig2 = sig2 * env;
	Out.ar(~masterBus, sig2);
}).add;

SynthDef( \noiseGhost, {
	arg freq=220, gate=1, amp=1.0, slideTime = 1.0;
	var sig, sig2, env;
	freq = Lag.kr(freq, slideTime);
	sig = Resonz.ar(Crackle.ar(1.98!2), freq, 0.04, 12) +
	Resonz.ar(WhiteNoise.ar(0.6!2), freq * 2, 0.01, 6) +
	Resonz.ar(WhiteNoise.ar(0.2!2), 300, 0.001, 4) +
	Resonz.ar(WhiteNoise.ar(0.1!2), 870, 0.001, 2) +
	Resonz.ar(WhiteNoise.ar(0.04!2), 2250, 0.001, 1);
	sig = sig * amp;
	sig = Splay.ar(sig, spread:0.9);
	env = EnvGen.kr(Env.asr(8, 1, 8, \sine), gate:gate, doneAction:2);
	sig = sig * env;
	Out.ar(~masterBus, sig);
}).add;

SynthDef( \jiGhost, {
	arg freq=220, jiFreq=110, gate=1, amp=1.0, slideTime = 2.0;
	var sig, sig2, env, jiOsc1, jiOsc2, jiOsc3, ampOsc, sirenSig, jiSig;
	freq = Lag.kr(freq, slideTime);
	// fundamental osc:
	jiOsc1 = (SinOsc.kr((0.01!2), 0, 0.5, 0.5) * jiFreq * 10/9) + jiFreq;
	// formant osc:
	jiOsc2 = (SinOsc.kr((0.133!2), 0, 0.5, 0.5) * jiFreq) + jiFreq;
	// width osc:
	jiOsc3 = (SinOsc.kr((0.2!2), 0, 0.5, 0.5) * jiFreq * 8) + jiFreq;
	ampOsc = SinOsc.ar(freq, 0, 0.5, 0.5);
	sirenSig = Formant.ar(jiOsc1, freq , jiOsc3, 0.04);
	jiSig = Formant.ar(jiFreq, jiOsc2, freq * 2, 0.25 * ampOsc);
	sig = jiSig + sirenSig;
	//sirenSig = sirenSig +
	sig = sig + Klank.ar(`[[freq, jiFreq, freq*2, 800, 1071, 1353, 1723], nil, [1, 1, 1, 1, 1, 1, 1]], PinkNoise.ar(0.001));
	sig = Splay.ar(sig, spread:0.9);
	env = EnvGen.kr(Env.asr(8, 1, 8, \sine), gate:gate, doneAction:2);
	sig = sig * env;
	Out.ar(~masterBus, sig);
}).add;

SynthDef( \claw, {
	arg freq = 2000, amp=0.6; // assume gate is not needed since there is no sustain
	var vibFreq, sig, sig2, env, release=3;
	vibFreq = Vibrato.kr((freq!2), rate:12, depth:0.09, delay:0.01, onset:0.6, rateVariation:0.1, depthVariation:0.2);
	sig = Saw.ar(vibFreq);
	sig = sig + SinOsc.ar(vibFreq * 2, mul:0.6);
	env = EnvGen.kr(Env.perc(0.02, release, 1, -22), doneAction:2);
	sig = LPF.ar(sig, 11000);
	sig = sig * env * amp;
	sig = Splay.ar(sig, spread:0.9);
	Out.ar(~masterBus, sig);
}).add;

SynthDef( \jiGhost2,{
	arg freq=55, amp=1.0, gate=1;
	var sig, sig2, env;
	sig = Saw.ar((freq!2), 0.09) + LFCub.ar(freq*2,mul:0.04) + LFCub.ar(freq*3,mul:0.02) + LFCub.ar(freq*4,mul:0.01);
	sig = LPF.ar(sig, freq*16);
	sig = sig * amp;
	env = EnvGen.kr(Env.asr(24, 1, 36, \sine), gate:gate, doneAction:2);
	sig = sig * env;
	Out.ar(~masterBus, sig);
}).add;

~masterOut = Synth("masterOut");

"====LOADED SYNTHS===="
)

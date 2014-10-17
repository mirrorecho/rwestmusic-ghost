

s.boot


(
var freq =  440;
{
	Resonz.ar(Crackle.ar(1.98), freq, 0.04, 8) +
	Resonz.ar(WhiteNoise.ar(0.4), freq * 2, 0.01, 6) +
	SinOscFB.ar(250, mul:0.03) +
	SinOsc.ar(595, mul: 0.02);
}.play;
)

(

SynthDef(\smoothGhosts, {
	arg moveHz=4, hiFreq=440, loFreq=880, gate=1, amp=1.0;
	var freq, sig, sig2, env, ghostCount=22;
	freq = LFNoise2.kr(moveHz!ghostCount).exprange(loFreq, hiFreq);
	amp = LFNoise2.kr(moveHz!ghostCount).exprange(0.01, 1.0);
	//amp = amp / (ghostCount/2);
	sig = SinOsc.ar(freq) * amp;
	sig2 = Splay.ar(sig, spread:0.6);
	sig2 = FreeVerb2.ar(sig2[0], sig2[1], mix:0.1);
	env = EnvGen.kr(Env.asr, gate:gate, doneAction:2);
	sig2 = sig2 * env;
	Out.ar(0, sig2);
}).add;

// TO DO... credit inspiration....!

)

(
var loFreq = 440;
var hiFreq = 440 * 2;

x = Synth.new(\smoothGhosts, [
	\moveHz, 8,
	\amp, 0.6,
	\loFreq, loFreq,
	\hiFreq, hiFreq]);
)

(
x.set(\gate, 0);
)
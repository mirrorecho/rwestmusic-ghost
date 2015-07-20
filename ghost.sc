// formant synthesis... better to look at examples here: "http://sccode.org/tag/category/formant synthesis"

// and here: "http://www.sussex.ac.uk/Users/nc81/modules/cm1/scfiles/12.2 Singing Voice Synthesis.html"

// SYNTHS:
// do harmonic analysis on flute... and then bend it
// sine (or other) waves with crazy vibrato
// smoothGhosts: constantly changing pitches of several sine waves

~ta = ();
~ta["breath-in"] = "ppp";

b = SoundFile.collectIntoBuffers("/home/randall/Echo/Sounds/Library/breath-in/*");

SoundFile.collect("/home/randall/Echo/Sounds/Library/breath-in/*").do { arg file;
	file.path.basename.splitext[0]
};


(
~libraryPath = "/home/randall/Echo/Sounds/Library/";

~breathFiles = [
    "breath-in/small-01.wav", 
    "breath-in/small-02.wav", 
    ];

~breaths=[];

~breathFiles.do { arg bFile;
    ~breaths = ~breaths ++ [Buffer.read(s, ~libraryPath ++ bFile)];
}
)


~breaths[0].play;

(
SynthDef(\breather, {arg bufnum;
	sig = PlayBuf.ar(2,
		bufnum:bufnum,
		doneAction:2
		);
}).add;
)

Server.killAll;

(
var freq =  120;
{
	Resonz.ar(Crackle.ar(1.98!2), freq, 0.04, 12) +
	Resonz.ar(WhiteNoise.ar(0.6!2), freq * 2, 0.01, 6) +
	Resonz.ar(WhiteNoise.ar(0.2!2), 300, 0.001, 4) +
	Resonz.ar(WhiteNoise.ar(0.1!2), 870, 0.001, 2) +
	Resonz.ar(WhiteNoise.ar(0.04!2), 2250, 0.001, 1);
}.play;
)


(
SynthDef(\wobbleGhost, {
	arg wobbleHz = 12, spread=0.125, freq=440, amp=0.6, gate=1;
	var sig1, sig2, wobbleSig, wobbleRate, sigOut, env;
	wobbleRate = LFNoise2.kr(1!2).range(wobbleHz, wobbleHz * 1.5);
	wobbleSig = SinOsc.kr(wobbleRate, mul:spread * freq);
	sig1 = SinOsc.ar((freq * 0.98) + wobbleSig[0], mul:amp);
	sig2 = SinOsc.ar((freq * 1.02) + wobbleSig[1], mul:amp);
	sigOut = Splay.ar([sig1, sig2], spread:0.8);
	sigOut = FreeVerb2.ar(sigOut[0], sigOut[1], mix:0.4);
	env = EnvGen.kr(Env.asr, gate:gate, doneAction:2);
	sigOut = sigOut * env;
	Out.ar(0, sigOut);
}).add;
)

(
x = Synth.new(\wobbleGhost, [
	\wobbleHz, 2,
	\spread, 0.125,
	\amp, 0.6,
	\freq, 440
]);
)

(
x.set(\gate, 0);
)


(

SynthDef(\smoothGhosts, {
	arg moveHz=4, loFreq=440, hiFreq=880, gate=1, amp=1.0;
	var freq, sig, sig2, env, ghostCount=22;
	freq = LFNoise2.kr(moveHz!ghostCount).exprange(loFreq, hiFreq);
	amp = LFNoise2.kr(moveHz!ghostCount).exprange(0.01, 1.0);
	//amp = amp / (ghostCount/2);
	sig = SinOsc.ar(freq) * amp;
	sig2 = Splay.ar(sig, spread:0.9);
	sig2 = FreeVerb2.ar(sig2[0], sig2[1], mix:0.4);
	env = EnvGen.kr(Env.asr, gate:gate, doneAction:2);
	sig2 = sig2 * env;
	Out.ar(0, sig2);
}).add;

// TO DO... credit inspiration....!

)

(
var loFreq = 220;
var hiFreq = loFreq * 4;

x = Synth.new(\smoothGhosts, [
	\moveHz, 1,
	\amp, 0.6,
	\loFreq, loFreq,
	\hiFreq, hiFreq]);
)

(
x.set(\gate, 0);
)
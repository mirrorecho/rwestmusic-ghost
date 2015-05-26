

// TO DO
// create the "ghost" as a synth def:
// - - formant synth
// - - with some flute sounds
// - - walk through circle of 5ths in random walk
// - - constant relationship to A & E
// - - some other pitched sounds (piano?)
// - - random flourishes, clangs, and creepers

// able to kick off 2 "ghosts"
// - - defined period of time

// shape the story
// - - input data from a "ghost" source
// - - can choose length, story shape, and possibly source material

// create score in python/abjad and lilypond
// - - current pitch combo AND simple markov process determines cues


// mastering, etc.
// - - reverb
// - - amplification, reaction to flute sounds


// create standalone application for mac OSX

// look at weirdo.sc (in sandbox)... bloo is a nice synth

// Use an envelope to pan notes from left to right and back


p.stop;



s.boot;

(
p = Pbind(
    \degree, Pif(Ptime(inf) < 4.0, Pwhite(-4, 11, inf)),
    \dur, 0.25
).play;
)

(

SynthDef(\wobbleGhost, {
	arg wobbleHz = 4, spread=0.125, freq=120, slideTime = 3.0, amp=0.6, gate=1;
	var sig1, sig2, wobbleSig, wobbleRate, sigOut, env;
	freq = Lag.kr(freq, slideTime);
	amp = Lag.kr(amp, slideTime);
	wobbleHz = Lag.kr(wobbleHz, slideTime);
	wobbleRate = LFNoise2.kr(1!2).range(wobbleHz, wobbleHz * 1.5);
	wobbleSig = SinOsc.kr(wobbleRate, mul:spread * freq);
	sig1 = SinOsc.ar((freq * 0.98) + wobbleSig[0], mul:amp * 0.5);
	sig2 = SinOsc.ar((freq * 1.02) + wobbleSig[1], mul:amp * 0.5);
	sigOut = Splay.ar([sig1, sig2], spread:0.8);
	sigOut = FreeVerb2.ar(sigOut[0], sigOut[1], mix:0.4);
	env = EnvGen.kr(Env.adsr(2, 2, 0.5, 4, 1, \sine), gate:gate, doneAction:2);
	sigOut = sigOut * env;
	Out.ar(0, sigOut);
}).add;

SynthDef(\smoothGhosts, {
	arg moveHz=4, loFreq=440, hiFreq=880, gate=1, amp=1.0;
	var freq, sig, sig2, env, ghostCount=22;
	freq = LFNoise2.kr(moveHz!ghostCount).exprange(loFreq, hiFreq);
	amp = LFNoise2.kr(moveHz!ghostCount).exprange(0.01, amp);
	//amp = amp / (ghostCount/2);
	sig = SinOsc.ar(freq) * amp;
	sig2 = Splay.ar(sig, spread:0.9);
	sig2 = FreeVerb2.ar(sig2[0], sig2[1], mix:0.4);
	env = EnvGen.kr(Env.asr, gate:gate, doneAction:2);
	sig2 = sig2 * env;
	Out.ar(0, sig2);
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
	sig2 = Splay.ar(sig, spread:0.9);
	sig2 = FreeVerb2.ar(sig2[0], sig2[1], mix:0.4);
	env = EnvGen.kr(Env.asr(8, 1, 8, \sine), gate:gate, doneAction:2);
	sig2 = sig2 * env;
	Out.ar(0, sig2);
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
	sig2 = Splay.ar(sig, spread:0.9);
	sig2 = FreeVerb2.ar(sig2[0], sig2[1], mix:0.9);
	env = EnvGen.kr(Env.asr(8, 1, 8, \sine), gate:gate, doneAction:2);
	sig2 = sig2 * env;
	Out.ar(0, sig2);
}).add;


SynthDef( \jiGhost2,{
	arg freq=55, amp=1.0, gate=1;
	var sig, sig2, env;
	sig = Saw.ar((freq!2), 0.09) + LFCub.ar(freq*2,mul:0.04) + LFCub.ar(freq*3,mul:0.02) + LFCub.ar(freq*4,mul:0.01);
	sig = LPF.ar(sig, freq*16);
	sig = sig * amp;
	sig2 = FreeVerb2.ar(sig[0], sig[1], mix:1.0);
	env = EnvGen.kr(Env.asr(24, 1, 36, \sine), gate:gate, doneAction:2);
	sig2 = sig2 * env;
	Out.ar(0, sig2);
}).add;


)



)


(
var numSections = 12;
var jiNote = -4;
var jiFreq = (jiNote + 60).midicps;
var jiStack = [0, 7];
var noteCycle = [0,7,2,9,4,11,6,1,8,3,10,5]; //circle of fifths
var sectionLengths = [9,12];
var refNote = 0;
var refLength = 12; // the length between each cycle...

p = Ptpar([
	// just a way to set global data....
	refLength, Pbind(*[
		note: \rest,
		myNote:Pwalk(
			(noteCycle + jiNote + 6),
			Prand([-2,-1, 0, 1, 1, 1, 2], inf)
		).collect( {arg x; refNote = x; postln(refNote); } ),
		dur: Prand(sectionLengths, numSections).collect( {arg x; refLength = x; } ),
	]),
	0, Pmono(*[
		\jiGhost2,
		freq:jiFreq / 4,
		dur: Pfuncn({ refLength }, numSections),
		amp:0.3,
	]),
	refLength, Pmono(*[
		\wobbleGhost,
		wobbleHz:Pwhite(1, 8),
		note:Pfuncn({ refNote + 36 }, numSections),
		dur: Pfuncn({ refLength }, numSections),
		amp: Prand([0.0001, 0.006, 0.01, 0.03], numSections),
	]),
	refLength, PmonoArtic(*[
		\jiGhost,
		jiFreq: jiFreq / 4,
		note: Pwrand([Pfuncn({ refNote }, 1), \rest], [0.7, 0.3], numSections),
		dur: Pfunc { refLength },
		amp:[0.9],
	]),
	refLength, PmonoArtic(*[
		\noiseGhost,
		note: Pwrand([Pfuncn({ refNote +.t jiStack }, 1), \rest], [0.4, 0.6], numSections),
		dur: Pfunc { refLength },
		amp:[0.4, 0.2],
	]),
]).trace.play;

)

p.stop;


(
var pitchLines = (
	ghost: Pser([1, 0, -3], inf),
);

var rhythms = (
	ghosts: [
		Pser([0.25, 0.75, 1], 3),
		Pser([0.25, 0.25, 1.5], 3)
	]
);

p = Pbind(*[
	instrument: \noiseGhost,
	note: pitchLines.ghost,
	dur: Prand(rhythms.ghosts, 8)
	]).play;
)

p.play;
p.stop;



Pnsym1
Pwalk
Prand
Pfunc
Ppar
Place
Ppatlace
Pseries
Pser
Pwhite
Pchain

// example of easy table creation:
[1,2,3,4] +.t [0,7];


// Flock of Seagulls!
(
p = Pbind(
    \degree, Pslide((-6, -4 .. 12), 8, 3, 1, 0),
    \dur, Pseq(#[0.1, 0.1, 0.2], inf),
    \sustain, 0.15
).play;
)

t = 79;
t = 78;
t = 92;

(
q = Pmono(*[
	\wobbleGhost,
	midinote: Prand([t, 80, 80, 82, 82, 84, 85, 87], inf),
	dur: 0.4
]).play;
)

p.stop;
q.stop;

r = (instrument: \smoothGhosts, loFreq: 880, hiFreq:440, amp: 0.1, dur: 8.0).play;

r.stop;

Pseries



(
p = Pbind(*[
	instrument: Prand([\noiseGhost, \wobbleGhost], inf),
	midinote: (Prand([80, 80, 82, 82, 84, 85, 87, [80, 84], [80, 85], Rest], inf) / 2),
	//dur: Pwhite(0.2, 2.0)
	dur: Prand([0.2, 0.2, 0.2, 0.2, 0.4, 0.4, 0.4, 0.8], inf)
]).play;
)

p.play;
p.mute;
p.unmute;
p.reset;
p.stop;



(
    f = { 3.yield; };
    x = Routine({ f.loop });
    10.do({ x.next.postln })
)

(

g = Task {

	var ghost, pitchClassG, pitchClassH, lowPitchG, lowPitchH;

	Pbind
	Ppar

	ghost = { | pitchClass = 10 |
		// to do... play the pitches

		// to do... preference for staying on the same pitch...
		pitchClass = pitchClass + (rrand(-1, 1) * 7) % 12; // randomly move through circle of 5ths

	};

	pitchClassG = 10;
	pitchClassH = [0,1,2,3,5,6,7,8].choose;
	loop {
		postln([pitchClassG, pitchClassH]);

		lowPitchG = (pitchClassG + 45).midicps;
		lowPitchH = (pitchClassH + 45).midicps;
		x = Synth(\smoothGhosts, [
			\moveHz, 1,
			\amp, 0.6,
			\loFreq, lowPitchG,
			\hiFreq, lowPitchH]);

		rrand(3.0,8.0).wait;

		x.set(\gate, 0);

		pitchClassG = ghost.value(pitchClassG);
		pitchClassH = ghost.value(pitchClassH);


	}
};

g.start;

)

(
g.stop;

)



//waitVal = rrand(3.0,8.0);
//waitVal.wait;


//g = Routine( { f.loop } );
//h = Routine( { ghost.loop } );


// loop

)






(
// a SynthDef
SynthDef(\test, { | out, freq = 440, amp = 0.1, nharms = 10, pan = 0, gate = 1 |
    var audio = Blip.ar(freq, nharms, amp);
    var env = Linen.kr(gate, doneAction: 2);
    OffsetOut.ar(out, Pan2.ar(audio, pan, env) );
}).add;
)

(
Pbind(*[
	instrument:'test',
	freq: Prand([1, 1.2, 2, 2.5, 3, 4], inf) * 200,
	dur: 0.1
]).play;
)


(
a = 4;
a.do {a.postln;}
)

(1..9) ++ ([0.1, 0.2] * 10);

(
var ghost_settings1 = [\noiseGhost, [
	amp: 1.0,
	freq: 220
]];

//new(Synth, *l); // or...
Synth(*ghost_settings1);

)


(
x = Synth(\noiseGhost, [
	amp: 1.0,
	freq: 220
]);
)

(
w = Synth(\noiseGhost, [
	amp: 0.8,
	freq: 220 * (3/2)
]);
)

(
v = Synth(\noiseGhost, [
	amp: 0.6,
	freq: 440
]);
)


(
y = Synth.new(\wobbleGhost, [
	wobbleHz: 1,
	spread: 0.04,
	amp: 0.4,
	freq: 440
]);
)


(
z = Synth.new(\smoothGhosts, [
	moveHz: 1,
	amp: 0.4,
	loFreq: 220,
	hiFreq: 440]);
)

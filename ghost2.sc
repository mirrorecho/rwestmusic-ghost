

// TO DO
// more "ghosts" as a synth defs:
// - - with some flute sounds
// - - some other pitched sounds (piano?)

// other synth defs:
// - - sampled sounds
// - - for random flourishes, clangs, and creepers
// - - percussive sounds

// able to construct small figures

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





(
p = Pbind(
    \degree, Pif(Ptime(inf) < 4.0, Pwhite(-4, 11, inf)),
    \dur, 0.25
).play;
)

(

s.boot;

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

SynthDef( \claw, {
	arg freq = 2000, amp=0.6; // assume gate is not needed since there is no sustain
	var vibFreq, sig, sig2, env, release=3;
	vibFreq = Vibrato.kr((freq!2), rate:12, depth:0.09, delay:0.01, onset:0.6, rateVariation:0.1, depthVariation:0.2);
	sig = Saw.ar(vibFreq);
	sig = sig + SinOsc.ar(vibFreq * 2, mul:0.6);
	env = EnvGen.kr(Env.perc(0.02, release, 1, -22), doneAction:2);
	sig = LPF.ar(sig, 11000);
	sig = sig * env * amp;
	sig2 = Splay.ar(sig, spread:0.9);
	sig2 = FreeVerb2.ar(sig2[0], sig2[1], mix:0.2);
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





// GREAT envelope for swells!
Env.perc(attackTime:4, releaseTime:0.01, level:1, curve:4).test.plot;



(
var numSections = 12;
var jiNote = -4;
var jiFreq = (jiNote + 60).midicps;
var jiStack = [0, 7];
var noteCycle = [0,7,2,9,4,11,6,1,8,3,10,5]; //circle of fifths
var sectionLengths = [9,12];
var refNote = 0;
var refLength = 9; // the length between each cycle...


var pitchLines = (
	claw: Pser([1, 12, 21], inf),
);

var rhythms = (
	claws: [
		Pser([0.25, 0.75, 1], 3),
		Pser([0.25, 0.25, 1.5], 3)
	]
);


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
		amp:0.4,
	]),
	// THIS GUY NEEDS WORK:
/*	refLength,Pn(
		Pxrand([
			Pbind(*[
				instrument:\claw,
				amp:0.1,
				note:pitchLines.claw + Pfunc { refNote + 24 },
				dur:Prand(rhythms.claws, 3)
			]),
		]), */
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
		amp:[1.0],
	]),
	refLength, PmonoArtic(*[
		\noiseGhost,
		note: Pwrand([Pfuncn({ refNote +.t jiStack }, 1), \rest], [0.4, 0.6], numSections),
		dur: Pfunc { refLength },
		amp:[0.6, 0.4],
	]),
]).trace.play;

)

p.trace.play;
p.stop;




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
Pstep

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

// ----------------------------------------------------------------------------------------------------------
// great example from here: "http://doc.sccode.org/Tutorials/Streams-Patterns-Events1.html"

(
    s = Server.local;
    SynthDef(\help_SPE1, { arg i_out=0, freq;
        var out;
        out = RLPF.ar(
            LFSaw.ar( freq, mul: EnvGen.kr( Env.perc, levelScale: 0.3, doneAction: 2 )),
            LFNoise1.kr(2, 36, 110).midicps,
            0.1
        );
        //out = [out, DelayN.ar(out, 0.04, 0.04) ];
        4.do({ out = AllpassL.ar(out, 0.5, [0.05.rand, 0.05.rand], 4) });
        Out.ar( i_out, out );
    }).send(s);
)



(
// streams as a sequence of pitches
    var stream, dur;
    dur = 1/4;
    stream = Routine.new({
        loop({
            if (0.5.coin, {
                // run of fifths:
                24.yield;
                31.yield;
                36.yield;
                43.yield;
                48.yield;
                55.yield;
            });
            rrand(2,5).do({
                // varying arpeggio
                60.yield;
                #[63,65].choose.yield;
                67.yield;
                #[70,72,74].choose.yield;
            });
            // random high melody
            rrand(3,9).do({ #[74,75,77,79,81].choose.yield });
        });
    });
    Routine({
        loop({
            Synth(\help_SPE1, [ \freq, stream.next.midicps ] );
            dur.yield; // synonym for yield, used by .play to schedule next occurence
        })
    }).play
)

// ----------------------------------------------------------------------------------------------------------

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
v = Synth(\clang, [
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

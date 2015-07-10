
(
// STARTUP --- RUN FIRST
s.freeAll;
~masterTempoClock = TempoClock(92/60);
~masterBus = Bus.audio(s,2);
"ghost_synths.sc".loadRelative;
)


{var sig; sig = SinOsc.ar(440, mul:0.1); Out.ar(~masterBus, sig);}.play;


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




// STORY!!!
// Flute is slowly "discovered"
// still do the walking 5ths ()
// very quiet hidden flute sounds slowly uncovered
// flute becomes crazier and crazier
// little cues in the electro signal flute phrases
// flute vibrato or Flz. to match?

// electronic sounds of breathing in!

// TO DO
// play with sounds !!!! (use that to determine how to represent the structures in code)

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
// - - amplification, reaction to flute sounds

// create standalone application for mac OSX

// look at weirdo.sc (in sandbox)... bloo is a nice synth

// Use an envelope to pan notes from left to right and back



p.stop;

(
p = Pbind(
    \instrument, "flute",
    \degree, Pif( Ptime() < 4.0, Pwhite(-4, 11, inf)),
    \ibreath, Pwhite(0.001, 0.4),
    \legato, 0.01,
    \dur, 0.25
).play;
)

p.trace.play;
p.stop;

Ptime.help;

s.scope;
s.meter;

// change one parameter
(
Pbind(
    \degree, Pstep( Pseq([1, 2, 3, 4, 5]), 1.0).trace,
    \dur, Pseries(0.1, 0.1, 15)
).play;
)


p = Pbind(*[
	instrument: \flute,
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


// GREAT envelope for swells!
Env.perc(attackTime:4, releaseTime:0.01, level:1, curve:4).test.plot;

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

0.9.coin;


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


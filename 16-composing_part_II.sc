// SuperCollider Tutorial: 16. Composing a Piece, Part II
// "https://www.youtube.com/watch?v=oR4VZy2LJ60"

/// 1 ///
s.boot;

s.meter;
s.plotTree;

/// 2 ///
(
SynthDef(\bpfsaw, {
	arg atk=2, sus=0, rel=3, c1=1, c2=(-1),
	freq=500, amp=1, out=0,
	detune=0.2,
	cfmin=500, cfmax=2000, rqmin=0.1, rqmax=0.2, pan=0, cfhzmin=0.1,cfhzmax=0.3, lsf=200, ldb=0;
	var sig, env;
	env = EnvGen.kr(Env([0,1,1,0],[atk,sus,rel],[c1,0,c2]),doneAction:2);
	sig = {Saw.ar(freq * LFNoise1.kr(0.5,detune).midiratio)}!2;
	sig = BPF.ar(
		sig,
		{LFNoise1.kr(
			LFNoise1.kr(4).exprange(cfhzmin,cfhzmax)).exprange(cfmin, cfmax)}!2,
		{LFNoise1.kr(0.1).exprange(rqmin, rqmax)}!2
	);
	sig = BLowShelf.ar(sig, lsf, 0.5, ldb);
	sig = Balance2.ar(sig[0], sig[1], pan);
	sig = sig * env * amp;
	Out.ar(out, sig);
}).add;

//dealing with files:
SynthDef(\bpfbuf, {
	arg atk=0, sus=0, rel=3, c1=1, c2=(-1), buf=0, rate=1, spos=0, freq=440, rq=1, bpfmix=0, pan=0, amp=1, out=0;
	var sig, env;
	env = EnvGen.kr(Env([0,1,1,0],[atk,sus,rel],[c1,0,c2]),doneAction:2);
	sig = PlayBuf.ar(1, buf, rate*BufRateScale.ir(buf),startPos:spos); //one channel, see below
	sig = XFade2.ar(sig, BPF.ar(sig, freq, rq, 1/rq.sqrt), bpfmix*2-1);
	sig = sig * env;
	sig = Pan2.ar(sig, pan, amp);
	Out.ar(out, sig);
}).add;
)



~b1 = Buffer.read(s,"/home/pablo/MusicStudioPablo/SuperCollider_stuff/tutorials_Fieldsteel/buffer/Cajon/Cajon_Fill02.wav" );
~b2 = Buffer.read(s,"/home/pablo/MusicStudioPablo/SuperCollider_stuff/tutorials_Fieldsteel/buffer/Conga/Conga_Loop02(140BPM).wav" );
~b3 = Buffer.read(s, "/home/pablo/MusicStudioPablo/SuperCollider_stuff/tutorials_Fieldsteel/buffer/Djembe/Djembe_Fill02.wav");

// test
~b1.play;

//to check stereo or mono?
~b1.numChannels;

Synth(\bpfbuf, [\buf, ~b1]);

(
Synth(
	\bpfbuf,
	[
		\buf, ~b3,
		\atk, 2,
		\sus, 1,
		\rel, 2,
		\bpfmix, 0.5,
		\freq, 2000,
		\rq, 1/400,
		\rate, 1.0,
	]
);
)

//radomnize

(
rrand(4,10).do{
Synth(
	\bpfbuf,
	[
		\buf, [~b1, ~b2, ~b3].choose,
		\atk, exprand(2,3),
		\sus, 1,
		\rel, exprand(2,3),
		\bpfmix, 1,
		\freq, (Scale.lydian.degrees+69).choose.midicps * [1,2].choose,
		\rq, exprand(0.001, 0.008),
		\rate, exprand(0.6, 1.2),
		\pan, rrand(-0.5,0.5),
		\amp, exprand(4,6),
	]
);
}
)

//9:00
//collection and management of sound files
//using Dictionary

//example:
d = Dictionary.new;
d.add(\foo -> 10);
d.add(\bar -> 20);
d[\bar];

(
//creates the dictionary
b = Dictionary.new;
//add the path
PathName("/home/pablo/MusicStudioPablo/SuperCollider_stuff/tutorials_Fieldsteel/buffer").entries.do{
	arg subfolder;
	b.add(
		subfolder.folderName.asSymbol ->
		Array.fill(
			subfolder.entries.size,
			{
				arg i;
				Buffer.read(s, subfolder.entries[i].fullPath);
			}
		)
	);
};
)

//test
b[\Cajon][3].play;

(
rrand(4,10).do{
Synth(
	\bpfbuf,
	[
		\buf, b[\Cajon][[1,5,3].choose],
		\atk, exprand(2,3),
		\sus, 1,
		\rel, exprand(2,3),
		\bpfmix, 1,
		\freq, (Scale.lydian.degrees+69).choose.midicps * [1,2].choose,
		\rq, exprand(0.001, 0.008),
		\rate, exprand(0.6, 1.2),
		\pan, rrand(-0.5,0.5),
		\amp, exprand(4,6),
	]
);
}
)

//18:20
//change starting position
(
5.do{ //five synth at once
Synth(
	\bpfbuf,
	[
		\buf, b[\Conga][3],
		\atk, 2,
		\rek, 2,
	    \spos, rrand(20000,25000),
		\rate, 0.5 * exprand(0.98, 1.02),
	],
);
}
)

//processing input signals
(
SynthDef(\bpfsaw, {
	arg atk=2, sus=0, rel=3, c1=1, c2=(-1),
	freq=500, amp=1, out=0,
	detune=0.2,
	cfmin=500, cfmax=2000, rqmin=0.1, rqmax=0.2, pan=0, cfhzmin=0.1,cfhzmax=0.3, lsf=200, ldb=0;
	var sig, env;
	env = EnvGen.kr(Env([0,1,1,0],[atk,sus,rel],[c1,0,c2]),doneAction:2);
	sig = {Saw.ar(freq * LFNoise1.kr(0.5,detune).midiratio)}!2;
	sig = BPF.ar(
		sig,
		{LFNoise1.kr(
			LFNoise1.kr(4).exprange(cfhzmin,cfhzmax)).exprange(cfmin, cfmax)}!2,
		{LFNoise1.kr(0.1).exprange(rqmin, rqmax)}!2
	);
	sig = BLowShelf.ar(sig, lsf, 0.5, ldb);
	sig = Balance2.ar(sig[0], sig[1], pan);
	sig = sig * env * amp;
	Out.ar(out, sig);
}).add;

//dealing with files:
SynthDef(\bpfbuf, {
	arg atk=0, sus=0, rel=3, c1=1, c2=(-1), buf=0, rate=1, spos=0, freq=440, rq=1, bpfmix=0, pan=0, amp=1, out=0;
	var sig, env;
	env = EnvGen.kr(Env([0,1,1,0],[atk,sus,rel],[c1,0,c2]),doneAction:2);
	sig = PlayBuf.ar(1, buf, rate*BufRateScale.ir(buf),startPos:spos); //one channel, see below
	sig = XFade2.ar(sig, BPF.ar(sig, freq, rq, 1/rq.sqrt), bpfmix*2-1);
	sig = sig * env;
	sig = Pan2.ar(sig, pan, amp);
	Out.ar(out, sig);
}).add;

SynthDef(\reverb, {
	arg in, predelay=0.1, revtime=1.8, lpf=4500, mix=0.15, amp=1, out=0;
	var dry, wet, temp, sig;
	dry = In.ar(in, 2);
	temp = In.ar(in,2);
	wet = 0;
	temp = DelayN.ar(temp, 0, 2, predelay);
	16.do{
		temp = AllpassN.ar(temp, 0.05, {Rand(0.001,0.05)}!2, revtime);
		temp = LPF.ar(temp, lpf); //damping
		wet = wet + temp;
	};
	sig = XFade2.ar(dry, wet, mix*2-1, amp);
	Out.ar(out, sig);
}).add;
)

//see also tutorial 7
~reverbBus = Bus.audio(s,2);
~reverbSynth = Synth(\reverb, [\in, ~reverbBus]);

(
Synth(
	\bpfbuf,
	[
		\buf, b[\Conga][(0..4).choose],
		\rel, rrand(0.15,0.25),
		\rate, rrand(-2.0,2.0).midiratio,
		\out, ~reverbBus,
	]
);
)

//combined with patterns:
(
p = Pbind(
	\instrument, \bpfbuf,
	\dur, Pexprand(0.1,1),
	\buf, Pxrand(b[\Conga][(5..7)]++b[\Djembe][(0..2)], inf),
		\rel, Pexprand(0.01,0.5),
		\spos, Pwhite(10000,40000),
		\rate, Pwhite(-7.0, 7.0).midiratio,
		\amp, Pexprand(0.5,0.9),
		\out, ~reverbBus,
	).play;
)

p.stop; //when stop, we need to relaunch the Bus

//solution for that:
~createReverb = {~reverbSynth = Synth(\reverb, [\in, ~reverbBus])};
ServerTree.add(~createReverb);
ServerTree.removeAll;



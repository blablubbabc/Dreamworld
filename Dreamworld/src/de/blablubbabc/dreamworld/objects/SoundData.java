package de.blablubbabc.dreamworld.objects;

import org.bukkit.Sound;

public class SoundData {
	private Sound sound;
	private float volumn;
	private float pitch;

	public SoundData(Sound sound, float volumn, float pitch) {
		this.sound = sound;
		this.volumn = volumn;
		this.pitch = pitch;
	}

	public Sound getSound() {
		return sound;
	}

	public float getVolumn() {
		return volumn;
	}

	public float getPitch() {
		return pitch;
	}
}

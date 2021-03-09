package com.example.cmput301project;

public class Experiment_Manager {
	private ArrayList<Hashmap<int, Experiment>> experiments;
	private int key;

	public Experiment_Manager() {
		this.key = 0;
		Experiments = new ArrayList<>();
	}

	public void addExperiment(Experiment e) {
		this.key++;
		Hashmap<int, Experiment> h = new Hashmap<>();
		h = {this.key, e};
		Experiments.add(h);
	}

	public void delExperiment(int k) {
		for (int i = 0; i < Experiments.size(); i++) {
			if (Experiments.get(i).containsKey(k)) {
				Experiments.get(i).remove();
			}
		}
	}
}
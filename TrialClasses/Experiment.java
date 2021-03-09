package com.example.cmput301project;

public class Experiment {

	private int numCount, numNonNeg, numMeas, numBinomial;
	private boolean isPublished = false;
	protected String description, region;
	private ArrayList<Count_Trial> cTrials;
	private ArrayList<Binomial_Trial> bTrials;
	private ArrayList<Measurement_Trial> mTrials;
	private ArrayList<Non_Negative_Integer_Counts_Trial> nnTrials;

	public Experiment() {
		this.numCount = 0;
		this.numNonNeg = 0;
		this.numMeas = 0;
		this.numBinomial = 0;
		this.cTrials = new ArrayList<>();
		this.bTrials = new ArrayList<>();
		this.mTrials = new ArrayList<>();
		this.nnTrials = new ArrayList<>();
	}

	public void createNewCount(Count_Trial c) {
		this.cTrials.add(c);
		this.numCount++;
	}

	public void createNewNonNeg(Non_Negative_Integer_Counts_Trial n) {
		this.nnTrials.add(n);
		this.numNonNeg++;
	}

	public void createNewMeasurement(Measurement_Trial m) {
		this.mTrials.add(m);
		this.numMeas++;
	}

	public void createNewBinomial(Binomial_Trial b) {
		this.bTrials.add(b);
		this.numBinomial++;
	}

	public void publish() {
		isPublished = true;
	}

	public void unpublish() {
		isPublished = false;
	}

	public String end() {
		return;
	}

	public void upload() {

	}

	public void ignore() {

	}

	public void stats() {

	}

	public void histogram() {

	}

	public void plots() {
		
	}
}
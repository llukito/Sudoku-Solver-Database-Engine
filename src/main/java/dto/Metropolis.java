package dto;

public class Metropolis {
    private final String metropolis;
    private final String continent;
    private final long population;

    public Metropolis(String metropolis, String continent, long population) {
        this.metropolis = metropolis;
        this.continent = continent;
        this.population = population;
    }

    public String getMetropolis() {
        return metropolis;
    }

    public String getContinent() {
        return continent;
    }

    public long getPopulation() {
        return population;
    }
}
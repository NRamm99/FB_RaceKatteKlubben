package dk.race.racekatteklubben.presentation.request;

import dk.race.racekatteklubben.domain.model.Race;

public record UpdatePetRequest(int id, String name, Race race) {
}

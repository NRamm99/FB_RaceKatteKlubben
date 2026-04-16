package dk.race.racekatteklubben.presentation.request;

import dk.race.racekatteklubben.domain.model.Race;

public record CreatePetRequest(String name, Race race) {
}

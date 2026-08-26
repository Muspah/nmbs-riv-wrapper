package nl.ovdata.nmbs.composition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single rolling-stock vehicle (carriage/unit) within a {@link CompositionSegment}.
 *
 * <p>{@code uicCode}/{@code parentUicCode}/{@code materialNumber} identify the physical, real-world vehicle and
 * are only present in {@code lastPlanned} segments (a confirmed, actually-coupled composition); {@code
 * commercialPlanned} segments describe the expected material type without pinning down the exact physical unit.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MaterialUnit(
        @JsonProperty("parentMaterialTypeName") String parentMaterialTypeName,
        @JsonProperty("parentMaterialSubTypeName") String parentMaterialSubTypeName,
        @JsonProperty("materialTypeName") String materialTypeName,
        @JsonProperty("materialSubTypeName") String materialSubTypeName,
        @JsonProperty("materialSubTypeSuffix") String materialSubTypeSuffix,
        @JsonProperty("positionInComposition") Integer positionInComposition,
        @JsonProperty("tractionPosition") Integer tractionPosition,
        @JsonProperty("inDirection") Boolean inDirection,
        @JsonProperty("isLocoInUse") Boolean isLocoInUse,
        @JsonProperty("uicCode") String uicCode,
        @JsonProperty("parentUicCode") String parentUicCode,
        @JsonProperty("materialDiagram") String materialDiagram,
        @JsonProperty("materialNumber") Integer materialNumber,
        @JsonProperty("diagramServiceNumber") Integer diagramServiceNumber,
        @JsonProperty("pullPushType") String pullPushType,
        @JsonProperty("tractionType") String tractionType,
        @JsonProperty("typeDescriptionNl") String typeDescriptionNl,
        @JsonProperty("typeDescriptionFr") String typeDescriptionFr,
        @JsonProperty("subTypeDescriptionNl") String subTypeDescriptionNl,
        @JsonProperty("subTypeDescriptionFr") String subTypeDescriptionFr,
        @JsonProperty("isFirstClass") Boolean isFirstClass,
        @JsonProperty("isSecondClass") Boolean isSecondClass,
        @JsonProperty("isEquipedForMux") Boolean isEquippedForMux,
        @JsonProperty("withTractionCoupling") Boolean withTractionCoupling,
        @JsonProperty("withAutomaticCoupling") Boolean withAutomaticCoupling,
        @JsonProperty("withTowBar") Boolean withTowBar,
        @JsonProperty("isDrivingCabinetAvailable") Boolean isDrivingCabinetAvailable,
        @JsonProperty("canPassToNextUnit") Boolean canPassToNextUnit,
        @JsonProperty("seatsFirstClass") Integer seatsFirstClass,
        @JsonProperty("seatsSecondClass") Integer seatsSecondClass,
        @JsonProperty("seatsCoupeFirstClass") Integer seatsCoupeFirstClass,
        @JsonProperty("seatsCoupeSecondClass") Integer seatsCoupeSecondClass,
        @JsonProperty("standingPlacesFirstClass") Integer standingPlacesFirstClass,
        @JsonProperty("standingPlacesSecondClass") Integer standingPlacesSecondClass,
        @JsonProperty("lengthInMeter") Integer lengthInMeter,
        @JsonProperty("brutoWeight") Integer brutoWeightTonnes,
        @JsonProperty("hasAirco") Boolean hasAirco,
        @JsonProperty("hasHeating") Boolean hasHeating,
        @JsonProperty("hasFirstClassOutlets") Boolean hasFirstClassOutlets,
        @JsonProperty("hasSecondClassOutlets") Boolean hasSecondClassOutlets,
        @JsonProperty("hasTables") Boolean hasTables,
        @JsonProperty("hasToilets") Boolean hasToilets,
        @JsonProperty("hasPrmToilets") Boolean hasPrmToilets,
        @JsonProperty("hasAssistanceButtons") Boolean hasAssistanceButtons,
        @JsonProperty("hasFoldingSeats") Boolean hasFoldingSeats,
        @JsonProperty("hasReadingLights") Boolean hasReadingLights,
        @JsonProperty("hasSemiAutomaticInteriorDoors") Boolean hasSemiAutomaticInteriorDoors,
        @JsonProperty("hasWasteBins") Boolean hasWasteBins,
        @JsonProperty("hasRealTimeInformationScreens") Boolean hasRealTimeInformationScreens,
        @JsonProperty("hasCameras") Boolean hasCameras,
        @JsonProperty("hasServiceSection") Boolean hasServiceSection,
        @JsonProperty("hasPriorityPlaces") Boolean hasPriorityPlaces,
        @JsonProperty("hasBikeSection") Boolean hasBikeSection,
        @JsonProperty("hasLuggageSection") Boolean hasLuggageSection,
        @JsonProperty("hasPrmSection") Boolean hasPrmSection,
        @JsonProperty("isRestaurant") Boolean isRestaurant
) {
}

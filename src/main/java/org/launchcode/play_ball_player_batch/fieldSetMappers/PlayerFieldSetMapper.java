package org.launchcode.play_ball_player_batch.fieldSetMappers;

import org.launchcode.play_ball_player_batch.models.Player;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class PlayerFieldSetMapper implements FieldSetMapper<Player> {
    @Override
    public Player mapFieldSet(FieldSet fieldSet) throws BindException {

        return new Player(
            fieldSet.readString(0),         // player id
            fieldSet.readString(1),         // player last name
            fieldSet.readString(2),         // player first name
            fieldSet.readString(3),         // player bats left, right, both
            fieldSet.readString(4),         // player pitches left, right
            fieldSet.readString(5)          // player team
        );
    }
}

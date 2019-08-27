package no.nav.melosys.eessi.kafka.producers.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Periode {

    private LocalDate fom;
    private LocalDate tom;

}

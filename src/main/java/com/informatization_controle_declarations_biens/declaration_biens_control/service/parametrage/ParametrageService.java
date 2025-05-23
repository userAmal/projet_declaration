package com.informatization_controle_declarations_biens.declaration_biens_control.service.parametrage;

import com.informatization_controle_declarations_biens.declaration_biens_control.data.parametrage.IParametrageData;
import com.informatization_controle_declarations_biens.declaration_biens_control.entity.parametrage.Parametrage;
import com.informatization_controle_declarations_biens.declaration_biens_control.iservice.parametrage.IParametrageService;
import com.informatization_controle_declarations_biens.declaration_biens_control.projection.parametrage.ParametrageProjection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ParametrageService implements IParametrageService {

    @Autowired
    private IParametrageData parametrageData;

    @Override
    public List<Parametrage> findAll() {
        return parametrageData.findAll();
    }

    @Override
    public Optional<Parametrage> findById(Long id) {
        return parametrageData.findById(id);
    }

    @Override
    public Optional<ParametrageProjection> findProjectedById(Long id) {
        return parametrageData.findProjectedById(id);
    }


    @Override
    public Parametrage save(Parametrage parametrage) {
        return parametrageData.save(parametrage);
    }

    @Override
    public void deleteById(Long id) {
        parametrageData.deleteById(id);
    }

    @Override
    public Parametrage updateValeur(Long id, String valeur) {
        Optional<Parametrage> parametrageOpt = parametrageData.findById(id);
        if (parametrageOpt.isPresent()) {
            Parametrage parametrage = parametrageOpt.get();
            parametrage.setValeur(valeur);
            return parametrageData.save(parametrage);
        }
        return null;
    }

    @Override
    public Parametrage getByCode(String code) {
        List<Parametrage> result = parametrageData.searchByCodeOrDescription(code);
        return result.isEmpty() ? null : result.get(0);
    }


    
    

}

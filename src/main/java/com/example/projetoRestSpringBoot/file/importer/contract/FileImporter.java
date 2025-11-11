package com.example.projetoRestSpringBoot.file.importer.contract;

import com.example.projetoRestSpringBoot.dto.CursoDTO;
import com.example.projetoRestSpringBoot.dto.FuncionarioDTO;


import java.io.InputStream;
import java.util.List;

public interface FileImporter {
   List<FuncionarioDTO> importarFuncionarios (InputStream inputStream) throws Exception;
   List<CursoDTO> importarCursos (InputStream inputStream) throws Exception;
}

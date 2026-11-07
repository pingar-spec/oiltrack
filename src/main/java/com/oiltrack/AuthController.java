package com.oiltrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        // Credenciales fijas de prueba para el taller (puedes cambiarlas o conectarlas a la base de datos)
        if ("admin".equals(request.getUsername()) && "123456".equals(request.getPassword())) {
            String token = jwtUtil.generateToken(request.getUsername());
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Autenticación exitosa");
            respuesta.put("token", token);
            return ResponseEntity.ok(respuesta);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Credenciales inválidas");
        return ResponseEntity.status(401).body(error);
    }
}
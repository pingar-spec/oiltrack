package com.oiltrack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/oiltrack")
@CrossOrigin(origins = "*")
public class OilTrackController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @PostMapping("/servicios")
    public ResponseEntity<Object> registrarServicio(@RequestBody ServicioRequest request) {
        Cliente cliente = clienteRepository.findByTelefono(request.getTelefonoCliente())
                .orElseGet(() -> {
                    Cliente nuevo = new Cliente();
                    nuevo.setNombre(request.getNombreCliente());
                    nuevo.setTelefono(request.getTelefonoCliente());
                    return clienteRepository.save(nuevo);
                });

        Vehiculo vehiculo = vehiculoRepository.findByPlaca(request.getPlaca())
                .orElseGet(() -> {
                    Vehiculo nuevoV = new Vehiculo();
                    nuevoV.setPlaca(request.getPlaca());
                    nuevoV.setMarca(request.getMarca());
                    nuevoV.setModelo(request.getModelo());
                    nuevoV.setCliente(cliente);
                    return vehiculoRepository.save(nuevoV);
                });

        Servicio servicio = new Servicio();
        servicio.setVehiculo(vehiculo);
        servicio.setTipoAceite(request.getTipoAceite());
        servicio.setKilometraje(request.getKilometraje());
        servicio.setProximoCambioKm(request.getProximoCambioKm());
        servicio.setObservaciones(request.getObservaciones());
        servicio.setFechaServicio(LocalDate.now());

        Servicio servicioGuardado = servicioRepository.save(servicio);

        String qrData = "http://localhost:8080/api/oiltrack/vehiculo/" + vehiculo.getPlaca();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Servicio registrado con éxito");
        respuesta.put("servicio", servicioGuardado);
        respuesta.put("qrCodigo", qrData);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/servicios")
    public List<Servicio> listarServicios() {
        return servicioRepository.findAll();
    }

    @GetMapping("/vehiculo/{placa}")
    public ResponseEntity<Object> obtenerHistorialPorPlaca(@PathVariable String placa) {
        Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findByPlaca(placa);
        
        if (!vehiculoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Vehiculo vehiculo = vehiculoOpt.get();
        List<Servicio> historial = servicioRepository.findByVehiculo(vehiculo);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("vehiculo", vehiculo);
        respuesta.put("historialServicios", historial);

        return ResponseEntity.ok(respuesta);
    }

    public static class ServicioRequest {
        private String nombreCliente;
        private String telefonoCliente;
        private String placa;
        private String marca;
        private String modelo;
        private String tipoAceite;
        private Integer kilometraje;
        private Integer proximoCambioKm;
        private String observaciones;

        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

        public String getTelefonoCliente() { return telefonoCliente; }
        public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

        public String getPlaca() { return placa; }
        public void setPlaca(String placa) { this.placa = placa; }

        public String getMarca() { return marca; }
        public void setMarca(String marca) { this.marca = marca; }

        public String getModelo() { return modelo; }
        public void setModelo(String modelo) { this.modelo = modelo; }

        public String getTipoAceite() { return tipoAceite; }
        public void setTipoAceite(String tipoAceite) { this.tipoAceite = tipoAceite; }

        public Integer getKilometraje() { return kilometraje; }
        public void setKilometraje(Integer kilometraje) { this.kilometraje = kilometraje; }

        public Integer getProximoCambioKm() { return proximoCambioKm; }
        public void setProximoCambioKm(Integer proximoCambioKm) { this.proximoCambioKm = proximoCambioKm; }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }
}
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mappa Struttura - OikoNaos</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 20px;
            text-align: center;
        }

        h1 { color: #2c3e50; margin-bottom: 5px; }
        p { color: #7f8c8d; margin-bottom: 30px; }

        /* Contenitore stile "Foglio" */
        .map-container {
            background: #ffffff;
            border: 1px solid #dcdde1;
            border-radius: 8px;
            padding: 20px;
            max-width: 900px;
            margin: 0 auto;
            box-shadow: 0 4px 15px rgba(0,0,0,0.05);
        }

        /* Stile SVG */
        svg {
            width: 100%;
            height: auto;
            background-color: #eef2f5; /* Suolo esterno */
            border-radius: 4px;
        }

        /* Stile Edifici */
        .building-base {
            fill: #dfe6e9;
            stroke: #636e72;
            stroke-width: 2;
        }

        /* Stile Stanze (Risorse Condivise) */
        .room {
            fill: #fff;
            stroke: #0984e3;
            stroke-width: 1.5;
        }

        /* Stile Appartamenti Privati (Non interagibili) */
        .private-area {
            fill: #f1f2f6;
            stroke: #b2bec3;
            stroke-width: 1;
            stroke-dasharray: 4;
        }

        /* Testi */
        .text-building {
            font-size: 14px;
            font-weight: bold;
            fill: #2d3436;
            text-anchor: middle;
        }

        .text-room {
            font-size: 11px;
            fill: #0984e3;
            font-weight: bold;
            text-anchor: middle;
            pointer-events: none;
        }

        .text-note {
            font-size: 9px;
            fill: #636e72;
            text-anchor: middle;
        }

        /* Legenda */
        .legend {
            margin-top: 25px;
            display: flex;
            justify-content: center;
            gap: 20px;
            font-size: 14px;
            color: #555;
        }
        .legend span { display: inline-block; width: 12px; height: 12px; margin-right: 5px; border-radius: 2px; }
    </style>
</head>
<body>

<h1>Planimetria OikoNaos</h1>
<p>Consulta la posizione degli edifici e delle risorse condivise.</p>

<div class="map-container">
    <svg viewBox="0 0 800 500" xmlns="http://www.w3.org/2000/svg">

        <rect x="50" y="50" width="700" height="400" rx="15" fill="none" stroke="#2ecc71" stroke-width="2" stroke-dasharray="5,5" />
        <text x="730" y="440" font-size="12" fill="#27ae60" text-anchor="end" font-style="italic">Giardino Condominiale (Eventi all'aperto)</text>

        <g>
            <rect x="100" y="100" width="220" height="300" class="building-base" />
            <text x="210" y="90" class="text-building">EDIFICIO A</text>

            <rect x="120" y="120" width="180" height="100" class="room" />
            <text x="210" y="165" class="text-room">SALA STUDIO</text>
            <text x="210" y="180" class="text-note">(10 Postazioni)</text>

            <rect x="120" y="240" width="180" height="130" class="room" />
            <text x="210" y="305" class="text-room">PALESTRA</text>
            <text x="210" y="320" class="text-note">(Attrezzature condivise)</text>
        </g>

        <g>
            <rect x="380" y="100" width="220" height="300" class="building-base" />
            <text x="490" y="90" class="text-building">EDIFICIO B</text>

            <rect x="400" y="120" width="180" height="90" class="room" />
            <text x="490" y="165" class="text-room">CUCINA COMUNE</text>

            <rect x="400" y="230" width="180" height="140" class="room" />
            <text x="490" y="295" class="text-room">SALA TV / RELAX</text>
        </g>

        <g>
            <rect x="650" y="100" width="80" height="300" class="building-base" />
            <text x="690" y="90" class="text-building">EDIFICIO C</text>

            <rect x="660" y="110" width="60" height="280" class="private-area" />
            <text x="690" y="250" class="text-note" style="writing-mode: tb;">APPARTAMENTI</text>
        </g>

    </svg>

    <div class="legend">
        <div><span style="background:#fff; border:1px solid #0984e3;"></span>Risorse Prenotabili / Comuni</div>
        <div><span style="background:#dfe6e9; border:1px solid #636e72;"></span>Edifici</div>
        <div><span style="border:1px dashed #2ecc71;"></span>Area Esterna</div>
    </div>

    <div style="margin-bottom: 20px;">
        <a href="${pageContext.request.contextPath}/home.jsp"
           class="btn ghost">
            Torna alla Home
        </a>
    </div>
</div>

</body>
</html>
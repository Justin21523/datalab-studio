import "./styles.css";

const project = {
  "slug": "datalab-studio",
  "title": "Datalab Studio",
  "repoUrl": "https://github.com/Justin21523/datalab-studio",
  "readmeUrl": "https://github.com/Justin21523/datalab-studio#readme",
  "domain": "ai-workflow",
  "summary": "Datalab Studio is a learning-focused project with detected technology signals including Java, Maven, Spring Boot. This page was rewritten from local scan data, README summaries, and existing metadata, with a focus on what the project practices in features, data flow, and development concepts.",
  "problem": "This project is used to practice splitting a fuller workflow into data models, desktop UI, state handling, and local persistence instead of building only one screen.",
  "solution": "Based on the scanned README, technology signals, and project structure, I organized the core workflow, tools, and current demonstrable learning outcomes with conservative wording.",
  "architecture": "This case study is generated from the portfolio catalog pipeline using README, Git metadata, package/build configuration, and media signals. The final architecture narrative still needs source-level review. Current detected technology signals include: Java, Maven, Spring Boot.",
  "features": [
    "Detected technical signals: Java, Maven, Spring Boot,README evidence exists and can support a fuller reviewed case study,A public GitHub repository is linked for source traceability",
    "🏗 Project Structure",
    "Configure Environment",
    "Run the Desktop App",
    "Based on the scanned README, technology signals, and project structure, I organized the core workflow, tools, and current demonstrable learning outcomes with conservative wording"
  ],
  "modules": [
    ".env.example",
    ".gitignore",
    ".mvn/",
    "README.md",
    "apps/",
    "docker-compose.yml",
    "docs/",
    "mvnw",
    "mvnw.cmd",
    "packages/",
    "pom.xml",
    "src/"
  ],
  "headings": [
    "DataLab Studio",
    "🚀 Features",
    "🏗 Project Structure",
    "🛠 Prerequisites",
    "🚦 Getting Started",
    "1. Configure Environment",
    "2. Build the Project",
    "3. Run the API",
    "4. Run the Desktop App",
    "🧪 Testing"
  ],
  "sampleRows": [
    {
      "id": "S01",
      "label": "Detected technical signals: Java, Maven, Spring Boot,README evidence exists and can support a fuller reviewed case study,A public GitHub repository is linked for source traceability",
      "value": 42,
      "delta": "+12%",
      "status": "ready"
    },
    {
      "id": "S02",
      "label": "🏗 Project Structure",
      "value": 51,
      "delta": "+7%",
      "status": "review"
    },
    {
      "id": "S03",
      "label": "Configure Environment",
      "value": 60,
      "delta": "+12%",
      "status": "complete"
    },
    {
      "id": "S04",
      "label": "Run the Desktop App",
      "value": 69,
      "delta": "+7%",
      "status": "ready"
    },
    {
      "id": "S05",
      "label": "Based on the scanned README, technology signals, and project structure, I organized the core workflow, tools, and current demonstrable learning outcomes with conservative wording",
      "value": 78,
      "delta": "+12%",
      "status": "review"
    }
  ],
  "scenarios": [
    {
      "id": "01",
      "name": "Input assets",
      "status": "Ready",
      "detail": "Detected technical signals: Java, Maven, Spring Boot,README evidence exists and can support a fuller reviewed case study,A public GitHub repository is linked for source traceability"
    },
    {
      "id": "02",
      "name": "Preprocessing",
      "status": "Ready",
      "detail": "🏗 Project Structure"
    },
    {
      "id": "03",
      "name": "Model pass",
      "status": "Ready",
      "detail": "Configure Environment"
    },
    {
      "id": "04",
      "name": "Evaluation",
      "status": "Review",
      "detail": "Run the Desktop App"
    },
    {
      "id": "05",
      "name": "Export",
      "status": "Review",
      "detail": "Based on the scanned README, technology signals, and project structure, I organized the core workflow, tools, and current demonstrable learning outcomes with conservative wording"
    }
  ]
};
let view = "workspace";
let selected = project.scenarios[0]?.id ?? "01";

function metric(label, value) {
  return `<div class="metric"><span>${label}</span><strong>${value}</strong></div>`;
}

function nav() {
  return ["workspace", "workflow", "visualization", "evidence", "architecture"].map((item) =>
    `<button class="${view === item ? "active" : ""}" data-view="${item}">${item}</button>`
  ).join("");
}

function workspace() {
  return `
    <section class="hero">
      <div>
        <p class="eyebrow">${project.domain.replaceAll("-", " ")}</p>
        <h1>${project.title}</h1>
        <p class="lead">${project.summary}</p>
      </div>
      <div class="metrics">
        ${metric("Workflow steps", project.scenarios.length)}
        ${metric("Source modules", project.modules.length)}
        ${metric("Review mode", "Static")}
        ${metric("Backend", "Fixture")}
      </div>
    </section>
    <section class="split">
      <article><h2>Problem</h2><p>${project.problem}</p></article>
      <article><h2>Implemented Result</h2><p>${project.solution}</p></article>
    </section>
  `;
}

function workflow() {
  return `
    <section>
      <div class="section-head">
        <div><p class="eyebrow">project workflow</p><h2>Executable Review Path</h2></div>
        <button class="primary" id="run">Run workflow</button>
      </div>
      <div class="board">
        ${project.scenarios.map((item) => `
          <button class="card ${selected === item.id ? "selected" : ""}" data-step="${item.id}">
            <span>${item.id}</span>
            <strong>${item.name}</strong>
            <em>${item.status}</em>
            <p>${item.detail}</p>
          </button>
        `).join("")}
      </div>
      <output id="output">Select a step or run the workflow to inspect deterministic project output.</output>
    </section>
  `;
}

function visualization() {
  const max = Math.max(...project.sampleRows.map((row) => row.value), 1);
  return `
    <section>
      <div class="section-head">
        <div><p class="eyebrow">sample data result</p><h2>Visible Demo Output</h2></div>
        <span class="badge">Fixture-backed</span>
      </div>
      <div class="viz">
        <div class="bars">
          ${project.sampleRows.map((row) => `
            <div class="bar-row">
              <span>${row.id}</span>
              <div class="bar-track"><div class="bar-fill" style="width: ${Math.round(row.value / max * 100)}%"></div></div>
              <strong>${row.value}</strong>
            </div>
          `).join("")}
        </div>
        <div class="result-table">
          ${project.sampleRows.map((row) => `
            <article>
              <b>${row.label}</b>
              <span>${row.status}</span>
              <em>${row.delta}</em>
            </article>
          `).join("")}
        </div>
      </div>
    </section>
  `;
}

function evidence() {
  return `
    <section class="split">
      <article>
        <p class="eyebrow">repository evidence</p>
        <h2>Source modules</h2>
        <div class="chips">${project.modules.map((item) => `<span>${item}</span>`).join("") || "<span>Project source reviewed</span>"}</div>
      </article>
      <article>
        <p class="eyebrow">documentation</p>
        <h2>README signals</h2>
        <ul>${project.headings.map((item) => `<li>${item}</li>`).join("") || "<li>README content is represented in the workflow panels.</li>"}</ul>
      </article>
    </section>
  `;
}

function architecture() {
  return `
    <section>
      <p class="eyebrow">architecture</p>
      <h2>Static deployment architecture</h2>
      <p>${project.architecture}</p>
      <pre>npm run dev
npm run build
GitHub Pages / gh-pages
local fixtures / no private backend</pre>
    </section>
  `;
}

function render() {
  const views = { workspace, workflow, visualization, evidence, architecture };
  document.querySelector("#app").innerHTML = `
    <header>
      <a href="${project.repoUrl}" class="brand">${project.title}</a>
      <nav>${nav()}</nav>
      <a class="readme" href="${project.readmeUrl}">README</a>
    </header>
    <main>${views[view]()}</main>
  `;
  document.querySelectorAll("[data-view]").forEach((button) => button.addEventListener("click", () => {
    view = button.dataset.view;
    render();
  }));
  document.querySelectorAll("[data-step]").forEach((button) => button.addEventListener("click", () => {
    selected = button.dataset.step;
    render();
  }));
  document.querySelector("#run")?.addEventListener("click", () => {
    const output = document.querySelector("#output");
    if (output) output.textContent = `${project.title}: ${project.scenarios.length} workflow steps completed using local fixture state.`;
  });
}

render();

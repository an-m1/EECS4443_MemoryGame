
# Light/Dark Mode Memory Game App

## Overview

This **Memory Game** is an Android application designed to evaluate the effects of graphical user interface (GUI) themes—specifically dark mode and light mode—on user efficiency, battery consumption, and comfort during mobile gaming. Developed as part of a user-interface and human-computer interaction study at York University, the app provides insights into how visual design choices influence both usability and device performance across OLED and LCD smartphones.

---

## Purpose of the App

The app was created as a core tool for a research study titled *"Evaluating Dark Mode Efficiency in Interactive Gaming"*. The key objectives of this study were:

- **To compare** task efficiency (speed and accuracy) in dark vs. light mode.
- **To measure** battery consumption under each theme.
- **To analyze** user preferences in terms of comfort, readability, and eye strain.
- **To investigate** how display technology (OLED vs. LCD) influences user experience with GUI themes.

This app acts as the primary instrument for data collection in this experimental setting.

---

## App Features

- 🎮 **4x6 Memory Card Game Grid** – Users play a matching game with randomized pairs.
- 🌗 **Dark/Light Mode Toggle** – Switch between dark (#000000 background, #FFFFFF text) and light (#FFFFFF background, #000000 text) modes.
- ⏱️ **Timer and Error Counter** – Automatically records completion time and mismatched pairs.
- 🔋 **Battery Monitoring** – Uses Android’s BatteryManager API to track power usage per session.
- 📋 **Post-Session Survey** – Collects user-reported experience metrics including digital eye strain and visibility.

---

## How to Use the App

1. Sign the user testing consent form, confirming that you consent to participate in this study. Link to consent form: [https://docs.google.com/document/d/1FcnCv6tJolXa23gukLqPMuE60RuEVUNL/edit?usp=sharing&ouid=100861146226451250457&rtpof=true&sd=true].
2. **Launch the App** on a compatible Android device.
3. **Begin Game Session:**
   - You’ll see a 4x6 grid of face-down cards.
   - Tap to reveal two cards and try to find matching pairs.
   - The game records your moves, errors, and time taken.
4. **Theme Toggle:**
   - Navigate to the settings menu.
   - Switch between Dark Mode and Light Mode to begin the next session.
5. **Complete Surveys:**
   - After each game session, fill out a short questionnaire evaluating your comfort, visibility, and performance.
     - Link to the questionnaire [https://docs.google.com/forms/d/e/1FAIpQLSeud2WlgoCDm99dHJfhtwFDISDGqWH2An1hFlERBcrK_Qw-OA/viewform?usp=dialog].
   - At the end of both sessions, complete a final comparison survey.
     - Link to the final, post-testing questionnaire [https://docs.google.com/forms/d/e/1FAIpQLSfbszbsX7xBdO27zbzPdYVHfSxf5n-YHhAngbDJJoKkpGOSgg/viewform?usp=header].

> Ensure brightness is fixed and “Night Shift” or “Auto Brightness” is disabled for accurate testing.<br />

> View the results from each test here: [https://docs.google.com/spreadsheets/d/1xdF9J2CcoD3w4UHf5hvJAEHmclyy40tq5_S6niudwcw/edit?usp=sharing]

---

## Testing Devices

The app was tested using two devices with different screen technologies:

| Device            | Screen Type | OS Version | Battery |
|-------------------|-------------|------------|---------|
| Google Pixel 7 Pro | OLED        | Android 13 | 5000mAh |
| Motorola Moto G24 | LCD         | Android 13 | 5000mAh |

> Brightness was standardized to 200 nits using a lux meter.

---

## Study Summary

- **Participants:** 8 individuals aged 18–25.
- **Design:** Within-subjects (theme), between-subjects (OLED vs. LCD).
- **Metrics Collected:** Completion time, error rate, battery usage, and subjective comfort.
- **Statistical Tests Used:**
  - Paired t-tests (for time and error)
  - Chi-square tests (for preferences)
  - Mixed-design ANOVA (for UI mode vs. screen type interactions)

---

## Results Snapshot

- **Efficiency:** Light mode had higher task efficiency on both OLED and LCD.
- **Battery Drain:** OLED saved ~33% more battery in dark mode.
- **Preferences:** 75% of OLED users preferred dark mode for comfort; 65% of LCD users preferred light mode for readability.

---

## Future Recommendations

- Support real-time adaptive UI based on ambient light or user preference.
- Integrate more precise battery profiling tools.
- Expand to larger participant pools and longer gameplay durations.
- Note: Within the Settings activity, the Volume, Music, and Vibration features are currently non-functional, the sliders and switches are placeholders with no operational behavior. They were added solely to fill the empty space for visual balance within the interface. The Dark Mode toggle is the only working feature, as it is directly relevant to the research study. The other features are not pertinent to the study and were included for design purposes only.

---

## Developers

- Ankit Modhera  
- Elizaveta Essen  
- Alex Valdez  
- Huanrui Cao

(C) 2025 York University – Department of Electrical Engineering and Computer Science

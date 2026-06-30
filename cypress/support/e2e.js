// ***********************************************************
// This example support/e2e.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// ========================================================
// ĐOẠN CODE HÃM TỐC ĐỘ CYPRESS (LÀM CHẬM UI ĐỂ DỄ THEO DÕI)
// ========================================================
const COMMAND_DELAY = 1500; // Nghỉ 1.5 giây sau mỗi lệnh thao tác

for (const command of ['click', 'type', 'clear', 'check', 'uncheck']) {
    Cypress.Commands.overwrite(command, (originalFn, ...args) => {
        const origVal = originalFn(...args);
        
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve(origVal);
            }, COMMAND_DELAY);
        });
    });
}
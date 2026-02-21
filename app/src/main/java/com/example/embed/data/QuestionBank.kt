package com.example.embed.data

/**
 * In-memory repository of all quiz questions.
 *
 * Questions are grouped by domain for readability. IDs are assigned
 * sequentially and must remain stable across app versions because the
 * spaced-repetition engine persists [CardState] records keyed by [Question.id].
 *
 * All answer options are capped at 28 characters so they fit comfortably
 * in the 2×2 answer-card grid without truncation.
 */
object QuestionBank {

    val all: List<Question> = buildList {

        // ── MEMORY (IDs 1–8) ─────────────────────────────────────────────────

        add(Question(
            id = 1,
            text = "What does SRAM use to store each bit?",
            options = listOf(
                "A flip-flop circuit",
                "A charged capacitor",
                "A floating-gate transistor",
                "A magnetic domain"
            ),
            correctIndex = 0,
            explanation = "SRAM uses cross-coupled flip-flop circuits — fast but area-hungry. " +
                    "DRAM uses a capacitor per bit that leaks charge and must be refreshed periodically.",
            domain = Domain.MEMORY
        ))

        add(Question(
            id = 2,
            text = "How are peripheral registers accessed with memory-mapped I/O?",
            options = listOf(
                "Via load/store instructions",
                "Via IN/OUT CPU instructions",
                "Via DMA transfers only",
                "Via a separate address bus"
            ),
            correctIndex = 0,
            explanation = "With memory-mapped I/O, peripheral registers appear at addresses in the " +
                    "normal address space, so a regular load/store instruction accesses a hardware register — " +
                    "no special CPU instructions needed.",
            domain = Domain.MEMORY
        ))

        add(Question(
            id = 3,
            text = "What must happen before new data can be written to NOR FLASH?",
            options = listOf(
                "Erase the target page first",
                "Set the write-enable bit",
                "Disable interrupts globally",
                "Switch to RAM execution"
            ),
            correctIndex = 0,
            explanation = "NOR FLASH bits can only be cleared (set to 0) by an erase operation " +
                    "covering an entire page or sector. Writing without erasing first corrupts data — " +
                    "a common cause of failed firmware updates.",
            domain = Domain.MEMORY
        ))

        add(Question(
            id = 4,
            text = "What does a Memory Protection Unit (MPU) do?",
            options = listOf(
                "Limits task memory access",
                "Accelerates cache lookups",
                "Translates virtual addresses",
                "Compresses RAM contents"
            ),
            correctIndex = 0,
            explanation = "The MPU lets an RTOS configure per-task access permissions. If a task " +
                    "writes outside its allowed region, the CPU triggers a fault rather than silently " +
                    "corrupting another task's memory.",
            domain = Domain.MEMORY
        ))

        add(Question(
            id = 5,
            text = "What erase granularity does EEPROM offer that FLASH does not?",
            options = listOf(
                "Byte-level erase and rewrite",
                "Page-level erase only",
                "Sector-level erase only",
                "No erase step required"
            ),
            correctIndex = 0,
            explanation = "FLASH erase granularity is typically 512 B to 128 KB. EEPROM supports " +
                    "byte-level writes, which is ideal for small configuration records that change " +
                    "frequently.",
            domain = Domain.MEMORY
        ))

        add(Question(
            id = 6,
            text = "What is a cache miss?",
            options = listOf(
                "Requested data not in cache",
                "Data found in cache",
                "A DMA cache corruption event",
                "An invalid cache index read"
            ),
            correctIndex = 0,
            explanation = "A cache miss means the required data is absent from the fast cache. " +
                    "The processor stalls while fetching from slower main memory, increasing execution latency.",
            domain = Domain.MEMORY
        ))

        add(Question(
            id = 7,
            text = "Which linker section holds zero-initialised global variables?",
            options = listOf(
                ".bss",
                ".text",
                ".data",
                ".rodata"
            ),
            correctIndex = 0,
            explanation = ".bss (Block Started by Symbol) holds zero-initialised globals. " +
                    "The startup code clears this region before calling main(), so no initial " +
                    "values need to be stored in FLASH.",
            domain = Domain.MEMORY
        ))

        add(Question(
            id = 8,
            text = "In byte-addressable memory, each unique address points to:",
            options = listOf(
                "Exactly one byte",
                "A 32-bit word",
                "A full cache line",
                "A hexadecimal digit"
            ),
            correctIndex = 0,
            explanation = "Byte-addressable memory assigns a unique address to every individual byte. " +
                    "Most modern architectures are byte-addressable even when the data bus is 32 or 64 bits wide.",
            domain = Domain.MEMORY
        ))

        // ── INTERRUPTS (IDs 9–15) ────────────────────────────────────────────

        add(Question(
            id = 9,
            text = "What does Cortex-M do automatically when accepting an interrupt?",
            options = listOf(
                "Auto-stacks 8 registers",
                "Calls the ISR directly",
                "Sends a peripheral ACK",
                "Disables all interrupts"
            ),
            correctIndex = 0,
            explanation = "Cortex-M performs automatic exception entry: it pushes xPSR, PC, LR, " +
                    "R12, and R0–R3 onto the stack before jumping to the ISR, allowing ISRs to be " +
                    "written as normal C functions.",
            domain = Domain.INTERRUPTS
        ))

        add(Question(
            id = 10,
            text = "What defines interrupt latency?",
            options = listOf(
                "Time from IRQ to ISR entry",
                "Total ISR execution time",
                "Time to enable the interrupt",
                "Priority encoder delay"
            ),
            correctIndex = 0,
            explanation = "Interrupt latency is the response delay — from the moment the hardware " +
                    "signals a request to the moment the ISR begins executing. It includes pipeline " +
                    "flush, register stacking, and vector fetch time.",
            domain = Domain.INTERRUPTS
        ))

        add(Question(
            id = 11,
            text = "On Cortex-M, what does a lower NVIC priority number indicate?",
            options = listOf(
                "Higher urgency, can preempt",
                "Lower urgency, handled last",
                "The interrupt is masked",
                "Shares a handler with IRQ 0"
            ),
            correctIndex = 0,
            explanation = "Cortex-M uses an inverted priority scale: priority 0 is the highest. " +
                    "An interrupt with a lower number can preempt one currently being serviced if the " +
                    "difference exceeds the configured preemption bits.",
            domain = Domain.INTERRUPTS
        ))

        add(Question(
            id = 12,
            text = "What risk does a long Interrupt Service Routine introduce?",
            options = listOf(
                "Blocks lower-priority IRQs",
                "ISR stack overflow",
                "Auto-termination by NVIC",
                "Peripheral de-asserts IRQ"
            ),
            correctIndex = 0,
            explanation = "While an ISR runs, lower- or equal-priority interrupts are blocked. " +
                    "A slow ISR increases worst-case latency for all events, risking missed real-time " +
                    "deadlines. The common pattern is to set a flag and defer work to a task.",
            domain = Domain.INTERRUPTS
        ))

        add(Question(
            id = 13,
            text = "What is interrupt nesting?",
            options = listOf(
                "Higher-priority ISR preempts",
                "Multiple peripherals per IRQ",
                "Calling a function in an ISR",
                "One ISR for both edge types"
            ),
            correctIndex = 0,
            explanation = "Interrupt nesting occurs when a higher-priority interrupt preempts an " +
                    "already-running ISR. The CPU stacks the current ISR context and handles the " +
                    "higher-priority event first before resuming.",
            domain = Domain.INTERRUPTS
        ))

        add(Question(
            id = 14,
            text = "What does `volatile` prevent the compiler from doing with a variable?",
            options = listOf(
                "Caching reads in a register",
                "Placing it in CPU cache",
                "Sharing it between tasks",
                "Making it atomic"
            ),
            correctIndex = 0,
            explanation = "Without `volatile`, the compiler may cache the variable in a CPU register " +
                    "and never re-read it from memory. `volatile` forces a fresh load on every access — " +
                    "essential when an ISR can modify the variable at any time.",
            domain = Domain.INTERRUPTS
        ))

        add(Question(
            id = 15,
            text = "What is a spurious interrupt?",
            options = listOf(
                "An IRQ with no valid source",
                "An IRQ before init completes",
                "A fixed-rate periodic IRQ",
                "A debugger-generated IRQ"
            ),
            correctIndex = 0,
            explanation = "A spurious interrupt has no identifiable source by the time the CPU reads " +
                    "the interrupt controller. It can be caused by electrical noise or a race between " +
                    "software clearing a flag and the hardware re-asserting it.",
            domain = Domain.INTERRUPTS
        ))

        // ── PROTOCOLS (IDs 16–22) ────────────────────────────────────────────

        add(Question(
            id = 16,
            text = "What makes SPI faster than I²C for high-throughput transfers?",
            options = listOf(
                "Separate MOSI and MISO lines",
                "Uses device addressing",
                "Open-drain bus topology",
                "Built-in error correction"
            ),
            correctIndex = 0,
            explanation = "SPI has dedicated MOSI and MISO lines, enabling full-duplex transfers and " +
                    "speeds up to hundreds of MHz. I²C shares a single bidirectional data line (SDA), " +
                    "adding addressing and ACK overhead that limits throughput.",
            domain = Domain.PROTOCOLS
        ))

        add(Question(
            id = 17,
            text = "What does the UART start bit signal to the receiver?",
            options = listOf(
                "Start of a new data frame",
                "The baud rate in use",
                "Odd parity on next byte",
                "Previous frame acknowledged"
            ),
            correctIndex = 0,
            explanation = "UART idles high. A start bit is a forced low level that tells the receiver " +
                    "to begin sampling. The receiver then samples each subsequent bit at intervals " +
                    "determined by the agreed baud rate.",
            domain = Domain.PROTOCOLS
        ))

        add(Question(
            id = 18,
            text = "Why does CAN bus use differential signalling on CANH and CANL?",
            options = listOf(
                "High noise immunity",
                "Full-duplex on a single pair",
                "Faster signal propagation",
                "No common ground needed"
            ),
            correctIndex = 0,
            explanation = "Differential signalling makes CAN robust against common-mode noise: " +
                    "interference affects both wires equally and cancels when the receiver subtracts " +
                    "CANL from CANH — critical in automotive and industrial environments.",
            domain = Domain.PROTOCOLS
        ))

        add(Question(
            id = 19,
            text = "What does I²C clock stretching allow a slave to do?",
            options = listOf(
                "Pause transfer until ready",
                "Exceed 400 kHz SCL frequency",
                "Sample on falling SCL edge",
                "Change address mid-transfer"
            ),
            correctIndex = 0,
            explanation = "Clock stretching lets a slave hold SCL low to signal 'not ready'. The " +
                    "master detects this and waits before proceeding — useful for slow peripherals such " +
                    "as ADCs that need time between conversions.",
            domain = Domain.PROTOCOLS
        ))

        add(Question(
            id = 20,
            text = "What does a NAK in an I²C transaction indicate?",
            options = listOf(
                "Byte not received correctly",
                "Bus arbitration won",
                "Next slave is selected",
                "Transfer rate is being set"
            ),
            correctIndex = 0,
            explanation = "After each byte, the receiver pulls SDA low to ACK or releases it high " +
                    "to NAK. A NAK signals the master to abort or retry, providing per-byte error " +
                    "detection.",
            domain = Domain.PROTOCOLS
        ))

        add(Question(
            id = 21,
            text = "Which USB descriptor tells the host the device's class?",
            options = listOf(
                "Device descriptor",
                "String descriptor",
                "HID report descriptor",
                "Configuration descriptor"
            ),
            correctIndex = 0,
            explanation = "The device descriptor is the first structure the host reads after " +
                    "enumeration. It contains the vendor ID, product ID, and device class code used " +
                    "to load the correct driver.",
            domain = Domain.PROTOCOLS
        ))

        add(Question(
            id = 22,
            text = "What is the role of the SPI chip-select (CS) line?",
            options = listOf(
                "Activates one specific slave",
                "Clocks data into the slave",
                "Indicates transfer direction",
                "Resets slave internal state"
            ),
            correctIndex = 0,
            explanation = "Because SPI has no addressing scheme, the master asserts a dedicated CS " +
                    "line (active-low) to select a particular slave. Only the selected device drives " +
                    "MISO and latches MOSI; all others ignore the clock.",
            domain = Domain.PROTOCOLS
        ))

        // ── RTOS (IDs 23–29) ─────────────────────────────────────────────────

        add(Question(
            id = 23,
            text = "What causes priority inversion in an RTOS?",
            options = listOf(
                "Low-prio task holds a mutex",
                "Voluntary priority reduction",
                "Wrong priority assignment",
                "Task elevated on ISR entry"
            ),
            correctIndex = 0,
            explanation = "Priority inversion occurs when a high-priority task is blocked waiting " +
                    "for a mutex held by a low-priority task, and a medium-priority task preempts the " +
                    "low-priority task — effectively inverting the intended priority order. Priority " +
                    "inheritance is the standard mitigation.",
            domain = Domain.RTOS
        ))

        add(Question(
            id = 24,
            text = "What property does a mutex have that a binary semaphore lacks?",
            options = listOf(
                "Ownership enforcement",
                "Interrupt-safe API",
                "Priority inheritance",
                "Counting capability"
            ),
            correctIndex = 0,
            explanation = "A mutex enforces ownership: only the task that acquired it may release it. " +
                    "A binary semaphore has no such restriction and is typically used for signalling " +
                    "between tasks or from an ISR.",
            domain = Domain.RTOS
        ))

        add(Question(
            id = 25,
            text = "What triggers a preemptive context switch in an RTOS?",
            options = listOf(
                "A ready higher-priority task",
                "Only an explicit yield call",
                "Fixed tick interval only",
                "Equal CPU time distribution"
            ),
            correctIndex = 0,
            explanation = "A preemptive RTOS immediately switches to the highest-priority ready task. " +
                    "This can happen when a higher-priority task is unblocked — for example, by a " +
                    "semaphore post from an ISR — or when the running task voluntarily suspends.",
            domain = Domain.RTOS
        ))

        add(Question(
            id = 26,
            text = "What is the immediate consequence of an RTOS task stack overflow?",
            options = listOf(
                "Corrupts neighbouring memory",
                "Task creation is rejected",
                "The scheduler drops the task",
                "ISR returns to wrong context"
            ),
            correctIndex = 0,
            explanation = "Each RTOS task has a fixed stack allocation. Deep call chains or large " +
                    "local arrays can exhaust it. The overflow corrupts adjacent memory — typically " +
                    "another task's stack or the heap — causing unpredictable failures. Most RTOSes " +
                    "provide stack-watermark monitoring.",
            domain = Domain.RTOS
        ))

        add(Question(
            id = 27,
            text = "What does a counting semaphore initial value of 5 represent?",
            options = listOf(
                "5 concurrent users permitted",
                "5 tasks blocked on it",
                "Resets after 5 take ops",
                "Owned by task number 5"
            ),
            correctIndex = 0,
            explanation = "A counting semaphore tracks available resource units. An initial value of " +
                    "5 means five concurrent accesses are allowed. Each 'take' decrements the count; " +
                    "each 'give' increments it. At zero, further takes block.",
            domain = Domain.RTOS
        ))

        add(Question(
            id = 28,
            text = "Which condition is NOT one of the four Coffman deadlock conditions?",
            options = listOf(
                "Priority inversion",
                "Circular wait",
                "Hold and wait",
                "No preemption"
            ),
            correctIndex = 0,
            explanation = "The four Coffman conditions are: mutual exclusion, hold-and-wait, " +
                    "no preemption of resources, and circular wait. Priority inversion is a separate " +
                    "scheduling problem, not a deadlock condition.",
            domain = Domain.RTOS
        ))

        add(Question(
            id = 29,
            text = "Why must RTOS blocking calls not be made from within an ISR?",
            options = listOf(
                "ISRs cannot suspend safely",
                "ISRs lack privilege level",
                "Tick timer is off in ISR",
                "ISR stack is too small"
            ),
            correctIndex = 0,
            explanation = "ISRs are not scheduled tasks — they have no concept of blocking. Calling " +
                    "a blocking API from an ISR corrupts the RTOS kernel state. ISR-safe variants " +
                    "(e.g., xQueueSendFromISR in FreeRTOS) exist for this purpose.",
            domain = Domain.RTOS
        ))

        // ── POWER MANAGEMENT (IDs 30–35) ─────────────────────────────────────

        add(Question(
            id = 30,
            text = "What is the effect of clock gating an idle peripheral?",
            options = listOf(
                "No dynamic power dissipated",
                "Limits the max CPU frequency",
                "Syncs two clock domains",
                "Generates a stable reference"
            ),
            correctIndex = 0,
            explanation = "Dynamic power consumption is proportional to clock frequency and switching " +
                    "activity. Clock gating stops the clock to an idle peripheral, eliminating switching " +
                    "entirely. On most microcontrollers, peripheral clocks are individually enabled in " +
                    "the RCC registers.",
            domain = Domain.POWER
        ))

        add(Question(
            id = 31,
            text = "What does STM32 STOP mode shut down that SLEEP mode does not?",
            options = listOf(
                "PLL and HSE oscillators",
                "Only the CPU core",
                "All RAM contents",
                "Core voltage only"
            ),
            correctIndex = 0,
            explanation = "In STOP mode the PLL and HSE oscillators are disabled, cutting most " +
                    "dynamic power. SRAM and register contents are preserved. After wake-up, the " +
                    "startup code must re-initialise the clocks before peripherals work again.",
            domain = Domain.POWER
        ))

        add(Question(
            id = 32,
            text = "Best power strategy for a sensor transmitting once per minute?",
            options = listOf(
                "Sleep deeply between bursts",
                "Run CPU at full speed always",
                "Disable the radio entirely",
                "Maximise the supply voltage"
            ),
            correctIndex = 0,
            explanation = "Average current = (active current × active time + sleep current × sleep " +
                    "time) / total time. Spending 55 of every 60 seconds in deep sleep at a few µA " +
                    "dramatically reduces the average even if the active burst draws tens of mA.",
            domain = Domain.POWER
        ))

        add(Question(
            id = 33,
            text = "What is a voltage regulator's quiescent current (Iq)?",
            options = listOf(
                "Self-consumption at no load",
                "Max deliverable load current",
                "Min startup input voltage",
                "Output ripple at full load"
            ),
            correctIndex = 0,
            explanation = "Quiescent current flows through the regulator's own circuitry regardless " +
                    "of load. In sleep modes where the MCU draws only µAs, a regulator with high Iq " +
                    "can dominate the system's total current budget.",
            domain = Domain.POWER
        ))

        add(Question(
            id = 34,
            text = "Why must GPIO inputs not be left floating in a low-power design?",
            options = listOf(
                "Causes wasteful oscillation",
                "Increases trace capacitance",
                "Prevents valid logic reads",
                "Raises supply rail impedance"
            ),
            correctIndex = 0,
            explanation = "An undriven input pin hovers near the switching threshold, causing it to " +
                    "toggle rapidly with noise. Each toggle dissipates energy. Pull-up or pull-down " +
                    "resistors — or configuring unused pins as outputs — prevent this.",
            domain = Domain.POWER
        ))

        add(Question(
            id = 35,
            text = "Which wake sources work in the deepest Cortex-M sleep mode?",
            options = listOf(
                "WKUP pins, RTC, and watchdog",
                "Any peripheral interrupt",
                "External reset pin only",
                "NVIC software interrupts"
            ),
            correctIndex = 0,
            explanation = "In deep sleep (STANDBY/SHUTDOWN), most clocks and peripherals are off. " +
                    "Only dedicated wake-up pins, the RTC (running from its own oscillator), and the " +
                    "watchdog remain active. Full peripheral wake capability requires a less deep mode.",
            domain = Domain.POWER
        ))

        // ── FIRMWARE & CODE (IDs 36–44) ──────────────────────────────────────

        add(Question(
            id = 36,
            text = "What does an Intel HEX file contain that a raw BIN file lacks?",
            options = listOf(
                "Address and checksum records",
                "Compressed binary data",
                "Debug symbol information",
                "Architecture type metadata"
            ),
            correctIndex = 0,
            explanation = "Intel HEX is a text format where each line contains an address, byte " +
                    "count, data in hex ASCII, and a checksum. A BIN file is the raw binary with no " +
                    "metadata — there is no address information embedded.",
            domain = Domain.FIRMWARE
        ))

        add(Question(
            id = 37,
            text = "What does the SECTIONS command in a linker script define?",
            options = listOf(
                "Memory region assignments",
                "Compiler optimisation flags",
                "Source files to compile",
                "The interrupt vector layout"
            ),
            correctIndex = 0,
            explanation = "The SECTIONS command maps input sections from object files to output " +
                    "sections, specifying each section's load address in FLASH (LMA) and run address " +
                    "in RAM (VMA). The startup code copies .data from LMA to VMA at boot.",
            domain = Domain.FIRMWARE
        ))

        add(Question(
            id = 38,
            text = "In a little-endian system, where is the least significant byte stored?",
            options = listOf(
                "At the lowest address",
                "At the highest address",
                "In the most significant bit",
                "In the first cache line"
            ),
            correctIndex = 0,
            explanation = "Little-endian (used by ARM Cortex-M and x86) stores the least significant " +
                    "byte first (at the lowest address). Big-endian does the opposite and is common in " +
                    "network protocols.",
            domain = Domain.FIRMWARE
        ))

        add(Question(
            id = 39,
            text = "Consider the following snippet. What is the value of `reg`?\n\n```c\nuint32_t reg = 0x00000000;\nreg |= (1U << 5);\n```",
            options = listOf(
                "0x00000020",
                "0x00000005",
                "0x00000010",
                "0x00000040"
            ),
            correctIndex = 0,
            explanation = "1U << 5 shifts the value 1 left by 5 positions: binary 0b00100000 = 0x20. " +
                    "OR-ing this into reg sets bit 5 while leaving all other bits unchanged.",
            domain = Domain.FIRMWARE,
            hasCodeBlock = true
        ))

        add(Question(
            id = 40,
            text = "What does `volatile` tell the compiler about a variable?",
            options = listOf(
                "Always access from memory",
                "Store it in read-only FLASH",
                "Protect it with a mutex",
                "It is local to this function"
            ),
            correctIndex = 0,
            explanation = "`volatile` prevents the compiler from caching accesses in a register or " +
                    "reordering them. Without it, the compiler might read a hardware register once and " +
                    "reuse the cached value, missing subsequent hardware-driven changes.",
            domain = Domain.FIRMWARE
        ))

        add(Question(
            id = 41,
            text = "What does the following register operation do?\n\n```c\n#define GPIOA_ODR (*(volatile uint32_t*)0x40020014U)\nGPIOA_ODR &= ~(1U << 3);\n```",
            options = listOf(
                "Clears bit 3 of GPIOA_ODR",
                "Sets bit 3 of GPIOA_ODR",
                "Reads bit 3 of GPIOA_IDR",
                "Toggles bit 3 of GPIOA_ODR"
            ),
            correctIndex = 0,
            explanation = "~(1U << 3) creates a mask with every bit set except bit 3. AND-ing this " +
                    "with the register clears only bit 3. Because the register maps to GPIO output data, " +
                    "clearing bit 3 drives PA3 low.",
            domain = Domain.FIRMWARE,
            hasCodeBlock = true
        ))

        add(Question(
            id = 42,
            text = "What is the watchdog timer's primary function?",
            options = listOf(
                "Reset system if not kicked",
                "Precise microsecond timing",
                "Protect FLASH from erasure",
                "Monitor supply voltage"
            ),
            correctIndex = 0,
            explanation = "The watchdog must be 'kicked' (counter reloaded) periodically by running " +
                    "software. If the firmware hangs or enters an infinite loop, the watchdog expires " +
                    "and issues a hardware reset, recovering the system autonomously.",
            domain = Domain.FIRMWARE
        ))

        add(Question(
            id = 43,
            text = "What does `const uint8_t* data` promise about the function below?\n\n```c\nvoid process(const uint8_t* data, size_t len);\n```",
            options = listOf(
                "Function won't modify *data",
                "Pointer cannot be reassigned",
                "Data is stored in FLASH",
                "Data is passed by value"
            ),
            correctIndex = 0,
            explanation = "`const uint8_t*` means the pointed-to bytes are read-only from this " +
                    "function's perspective. The compiler will error if the function tries to write " +
                    "through the pointer, and callers know their buffer is safe to pass.",
            domain = Domain.FIRMWARE,
            hasCodeBlock = true
        ))

        add(Question(
            id = 44,
            text = "What is the JTAG TAP (Test Access Port) state machine's role?",
            options = listOf(
                "Controls IR and DR shifting",
                "Manages USB to PC connection",
                "Maps breakpoints to memory",
                "Delivers power to target"
            ),
            correctIndex = 0,
            explanation = "The TAP controller is a 16-state FSM driven by the TMS signal. It " +
                    "sequences whether TDI/TDO are shifting into the instruction register (IR) or a " +
                    "data register (DR), enabling scan-chain-based debug and boundary scan.",
            domain = Domain.FIRMWARE
        ))
    }

    /** Returns all questions belonging to the given set of domains. */
    fun forDomains(domains: Set<Domain>): List<Question> =
        all.filter { it.domain in domains }

    /** Looks up a single question by its ID; returns null if not found. */
    fun byId(id: Int): Question? = all.firstOrNull { it.id == id }
}